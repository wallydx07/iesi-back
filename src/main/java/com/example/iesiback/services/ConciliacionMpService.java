package com.example.iesiback.services;

import com.example.iesiback.dto.ConciliacionItemDTO;
import com.example.iesiback.dto.ConciliacionResultadoDTO;
import com.example.iesiback.dto.ConciliacionResumenDTO;
import com.example.iesiback.entities.Pago;
import com.example.iesiback.enums.EstadoPago;
import com.example.iesiback.enums.ResultadoConciliacion;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.net.MPResultsResourcesPage;
import com.mercadopago.net.MPSearchRequest;
import com.mercadopago.resources.payment.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.iesiback.enums.ResultadoConciliacion.*;

@Service
@RequiredArgsConstructor
public class ConciliacionMpService {

    private final PagoService pagoRepository;
    private final PaymentClient paymentClient = new PaymentClient();

    private static final ZoneId ZONA = ZoneId.of("America/Argentina/Buenos_Aires");

    public ConciliacionResultadoDTO conciliar(LocalDate desde, LocalDate hasta)
            throws MPException, MPApiException {

        Instant inicio = desde.atStartOfDay(ZONA).toInstant();
        Instant fin = hasta.plusDays(1).atStartOfDay(ZONA).toInstant();

        // ── Lado local ──
        List<Pago> locales = pagoRepository.findByFechaPagoBetween(inicio, fin);

        // ── Lado MP (search paginado) ──
        List<Payment> pagosMp = buscarPagosMp(inicio, fin);

        Map<String, Payment> mpPorId = pagosMp.stream()
                .collect(Collectors.toMap(
                        p -> String.valueOf(p.getId()), p -> p, (a, b) -> a));

        Map<String, Payment> mpPorExtRef = pagosMp.stream()
                .filter(p -> p.getExternalReference() != null)
                .collect(Collectors.toMap(
                        Payment::getExternalReference, p -> p, (a, b) -> a));

        List<ConciliacionItemDTO> items = new ArrayList<>();
        Set<Long> mpMatcheados = new HashSet<>();
        int efectivoNoAplica = 0;

        // ── Cruce local → MP ──
        for (Pago pago : locales) {
            String mpId = pago.getMpPaymentIdStr();

            // Sin id de MP → pago manual / efectivo, no se cruza
            if (mpId == null || mpId.isBlank()) {
                // Fallback: intentamos por external_reference igual
                Payment porRef = pago.getExternalReference() != null
                        ? mpPorExtRef.get(pago.getExternalReference())
                        : null;
                if (porRef == null) {
                    efectivoNoAplica++;
                    continue;
                }
                mpMatcheados.add(porRef.getId());
                items.add(compararPago(pago, porRef));
                continue;
            }

            Payment mp = mpPorId.get(mpId);

            // Puede haberse creado FUERA del rango en MP (ej. QR generado
            // ayer, pagado hoy) → lo buscamos individualmente
            if (mp == null) {
                mp = buscarPagoIndividual(mpId);
            }

            if (mp == null) {
                items.add(ConciliacionItemDTO.builder()
                        .pagoId(Long.valueOf(pago.getId()))
                        .mpPaymentId(mpId)
                        .externalReference(pago.getExternalReference())
                        .alumno(nombreAlumno(pago))
                        .estadoLocal(String.valueOf(pago.getEstado()))
                        .montoLocal(pago.getMontoTotal())
                        .resultado(ResultadoConciliacion.SOLO_LOCAL)
                        .detalle("El pago tiene ID de MP pero MP no lo devuelve. Verificar manualmente.")
                        .build());
                continue;
            }

            mpMatcheados.add(mp.getId());
            items.add(compararPago(pago, mp));
        }

        // ── Cruce MP → local: pagos en MP que no tenemos ──
        for (Payment mp : pagosMp) {
            if (mpMatcheados.contains(mp.getId())) continue;
            // Solo alertamos los que MP considera plata real o en curso
            String st = mp.getStatus();
            if (!"approved".equals(st) && !"pending".equals(st)
                    && !"in_process".equals(st) && !"refunded".equals(st)
                    && !"charged_back".equals(st)) {
                continue; // rejected/cancelled sin registro local no importan
            }
            items.add(ConciliacionItemDTO.builder()
                    .mpPaymentId(String.valueOf(mp.getId()))
                    .externalReference(mp.getExternalReference())
                    .estadoMp(st)
                    .estadoMpMapeado(String.valueOf(mapearEstadoMp(st)))
                    .montoMp(mp.getTransactionAmount())
                    .fechaMp(fechaMp(mp))
                    .metodoMp(mp.getPaymentMethodId())
                    .resultado(ResultadoConciliacion.SOLO_MP)
                    .detalle("MP registra este pago pero no existe en el sistema. Posible webhook perdido.")
                    .build());
        }

        return new ConciliacionResultadoDTO(armarResumen(items, efectivoNoAplica), items);
    }

    // ── Comparación individual ──
    private ConciliacionItemDTO compararPago(Pago pago, Payment mp) {
        String statusMp = mp.getStatus();
        EstadoPago estadoMpMapeado = mapearEstadoMp(statusMp);
        EstadoPago estadoLocal = pago.getEstado();

        BigDecimal montoLocal = pago.getMontoTotal() != null ? pago.getMontoTotal() : BigDecimal.ZERO;
        BigDecimal montoMp = mp.getTransactionAmount() != null ? mp.getTransactionAmount() : BigDecimal.ZERO;

        ResultadoConciliacion resultado;
        String detalle;

        boolean esReembolso = "refunded".equals(statusMp) || "charged_back".equals(statusMp);

        if (esReembolso && estadoLocal == EstadoPago.APROBADO) {
            resultado = ResultadoConciliacion.REEMBOLSADO;
            detalle = "⚠️ MP devolvió/contracargó este pago pero localmente sigue APROBADO.";
        } else if (estadoLocal != estadoMpMapeado) {
            resultado = ResultadoConciliacion.ESTADO_DISTINTO;
            detalle = "Local: " + estadoLocal + " / MP: " + statusMp + ".";
        } else if (montoLocal.compareTo(montoMp) != 0) {
            resultado = ResultadoConciliacion.MONTO_DISTINTO;
            detalle = "Local: $" + montoLocal + " / MP: $" + montoMp + ".";
        } else {
            resultado = ResultadoConciliacion.CONCILIADO;
            detalle = "OK";
        }

        return ConciliacionItemDTO.builder()
                .pagoId(Long.valueOf(pago.getId()))
                .mpPaymentId(String.valueOf(mp.getId()))
                .externalReference(pago.getExternalReference())
                .alumno(nombreAlumno(pago))
                .estadoLocal(String.valueOf(estadoLocal))
                .estadoMp(statusMp)
                .estadoMpMapeado(String.valueOf(estadoMpMapeado))
                .montoLocal(montoLocal)
                .montoMp(montoMp)
                .fechaMp(fechaMp(mp))
                .metodoMp(mp.getPaymentMethodId())
                .resultado(resultado)
                .detalle(detalle)
                .build();
    }



    private List<Payment> buscarPagosMp(Instant inicio, Instant fin)
            throws MPException, MPApiException {

        List<Payment> resultado = new ArrayList<>();
        int offset = 0;
        final int LIMIT = 100;
        int total;
        int guard = 0; // corta a 5000 pagos por las dudas

        do {
            Map<String, Object> filtros = new HashMap<>();
            filtros.put("range", "date_created");
            filtros.put("begin_date", inicio.toString()); // ISO-8601 con Z
            filtros.put("end_date", fin.toString());
            filtros.put("sort", "date_created");
            filtros.put("criteria", "asc");

            MPSearchRequest search = MPSearchRequest.builder()
                    .limit(LIMIT)
                    .offset(offset)
                    .filters(filtros)
                    .build();

            MPResultsResourcesPage<Payment> page = paymentClient.search(search);
            resultado.addAll(page.getResults());
            total = page.getPaging().getTotal();
            offset += LIMIT;
        } while (offset < total && ++guard < 50);

        return resultado;
    }


    private Payment buscarPagoIndividual(String mpId) {
        try {
            return paymentClient.get(Long.parseLong(mpId));
        } catch (Exception e) {
            return null; // 404 o id inválido → SOLO_LOCAL
        }
    }

    // ── Sincronizar: aplicar el estado de MP al pago local ──
    @Transactional
    public Pago sincronizarPago(Integer pagoId) throws MPException, MPApiException {
        Pago pago = pagoRepository.buscarPorId(pagoId)
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado: " + pagoId));

        if (pago.getMpPaymentIdStr() == null || pago.getMpPaymentIdStr().isBlank()) {
            throw new IllegalStateException("El pago no tiene ID de Mercado Pago.");
        }

        Payment mp = paymentClient.get(Long.parseLong(pago.getMpPaymentIdStr()));
        pago.setEstado(mapearEstadoMp(mp.getStatus()));
        return pagoRepository.guardar(pago);
    }

    // ── Helpers ──
    private EstadoPago mapearEstadoMp(String status) {
        if (status == null) return EstadoPago.PENDIENTE;
        return switch (status) {
            case "approved" -> EstadoPago.APROBADO;
            case "pending", "in_process", "authorized", "in_mediation" -> EstadoPago.PENDIENTE;
            case "rejected" -> EstadoPago.RECHAZADO;
            case "cancelled", "refunded", "charged_back" -> EstadoPago.CANCELADO;
            default -> EstadoPago.PENDIENTE;
        };
    }

    private String nombreAlumno(Pago pago) {
        // Adaptar a cómo resuelvas el alumno hoy (vía trámite → persona).
        // Si es caro, podés devolver solo el DNI del trámite:
        return pago.getTramite() != null && pago.getTramite().getTramiteDni() != null
                ? String.valueOf(pago.getTramite().getTramiteDni())
                : null;
    }

    private String fechaMp(Payment mp) {
        return mp.getDateCreated() != null
                ? mp.getDateCreated().atZoneSameInstant(ZONA)
                .format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"))
                : null;
    }

    private ConciliacionResumenDTO armarResumen(List<ConciliacionItemDTO> items, int efectivoNoAplica) {
        int conciliados = 0, discrepancias = 0, reembolsados = 0, soloMp = 0, soloLocal = 0;
        BigDecimal montoOk = BigDecimal.ZERO, montoDisc = BigDecimal.ZERO;

        for (ConciliacionItemDTO i : items) {
            BigDecimal m = i.getMontoMp() != null ? i.getMontoMp()
                    : (i.getMontoLocal() != null ? i.getMontoLocal() : BigDecimal.ZERO);
            switch (i.getResultado()) {
                case CONCILIADO -> { conciliados++; montoOk = montoOk.add(m); }
                case ESTADO_DISTINTO, MONTO_DISTINTO -> { discrepancias++; montoDisc = montoDisc.add(m); }
                case REEMBOLSADO -> { reembolsados++; montoDisc = montoDisc.add(m); }
                case SOLO_MP -> { soloMp++; montoDisc = montoDisc.add(m); }
                case SOLO_LOCAL -> { soloLocal++; montoDisc = montoDisc.add(m); }
            }
        }

        return ConciliacionResumenDTO.builder()
                .conciliados(conciliados)
                .discrepancias(discrepancias)
                .reembolsados(reembolsados)
                .soloMp(soloMp)
                .soloLocal(soloLocal)
                .efectivoNoAplica(efectivoNoAplica)
                .montoConciliado(montoOk)
                .montoDiscrepante(montoDisc)
                .build();
    }
}