package com.example.iesiback.services;

import com.example.iesiback.dto.CorrelativasFaltantesEstadoDTO;
import com.example.iesiback.dto.EvaluacionCorrelativaResponse;
import com.example.iesiback.dto.NotaCursandoProjection;
import com.example.iesiback.dto.NotaMateriaDTO;
import com.example.iesiback.entities.Materia;
import com.example.iesiback.enums.EstadoCondicion;
import com.example.iesiback.enums.EstadoNota;
import com.example.iesiback.repositories.CursadaRepository;
import com.example.iesiback.repositories.MateriaRepository;
import com.example.iesiback.repositories.NotaRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CorrelativaService {

    private final NotaRepository notaRepository;
    private final CursadaRepository cursadaRepository;
    private final MateriaRepository materiaRepository;


    // ═══════════════════════════════════════════════════════════════════════════
    // TIPOS DE DATOS INTERNOS
    // ═══════════════════════════════════════════════════════════════════════════

    public enum TipoVeredicto {
        ACEPTADA,
        PROVISORIA,
        SIN_CORRELATIVAS,
        SIN_CURSADA,
        FECHA_INCOHERENTE
    }

    @Value
    @Builder
    public static class ResultadoItem {
        String orden;
        boolean aprobada;
        boolean fechaOk;
        String detalle;
        LocalDate fechaAprobacion;

        static ResultadoItem ausente(String orden) {
            return ResultadoItem.builder()
                    .orden(orden)
                    .aprobada(false)
                    .fechaOk(false)
                    .detalle("Sin cursada registrada")
                    .build();
        }
    }

    @Value
    @Builder
    public static class VeredictoDoble {
        Veredicto cursada;
        Veredicto examen;

        public boolean rechazado() {
            return !cursada.esAceptada();
        }

        public String estado() {
            if (rechazado()) return "Rechazado";
            return examen.esAceptada() ? "Aceptada" : "Provisoria";
        }
    }

    /**
     * Evalúa CURSADA y EXAMEN en una sola carga de notas del legajo.
     * Evita doble query por alumno.
     */
    public Map<Integer, VeredictoDoble> evaluarLoteDoble(
            String legajoId,
            List<NotaMateriaDTO> materias) {

        // Una sola query para todo el legajo
        Map<String, List<NotaCursandoProjection>> notasMap = cargarNotasLegajo(legajoId);
        Map<Integer, VeredictoDoble> resultado = new LinkedHashMap<>();

        for (NotaMateriaDTO materia : materias) {
            String orden = String.valueOf(materia.getMateriaOrden());
            List<NotaCursandoProjection> notasMateria = notasMap.getOrDefault(orden, List.of());

            Veredicto vCursada = ejecutarConMapa(notasMateria, EstadoCondicion.CURSADA, notasMap);
            Veredicto vExamen  = ejecutarConMapa(notasMateria, EstadoCondicion.EXAMEN,  notasMap);

            resultado.put(materia.getMateriaOrden(),
                    VeredictoDoble.builder()
                            .cursada(vCursada)
                            .examen(vExamen)
                            .build());
        }
        return resultado;
    }

    @Value
    @Builder
    public static class Veredicto {
        TipoVeredicto tipo;
        List<ResultadoItem> items;

        public boolean esAceptada() {
            return tipo == TipoVeredicto.ACEPTADA
                    || tipo == TipoVeredicto.SIN_CORRELATIVAS
                    || tipo == TipoVeredicto.FECHA_INCOHERENTE;
        }

        public List<String> desaprobadas() {
            if (items == null || items.isEmpty()) return List.of("Ninguna");
            List<String> result = items.stream()
                    .filter(i -> !i.isAprobada())
                    .map(ResultadoItem::getOrden)
                    .collect(Collectors.toList());
            return result.isEmpty() ? List.of("Ninguna") : result;
        }

//        public List<String> conFechaIncoherente() {
//            if (items == null || items.isEmpty()) return List.of("No");
//            List<String> result = items.stream()
//                    .filter(i -> i.isAprobada() && !i.isFechaOk())
//                    .map(i -> i.getOrden() + " (" + i.getDetalle() + ")")
//                    .collect(Collectors.toList());
//            return result.isEmpty() ? List.of("No") : result;
//        }


        public List<String> conFechaIncoherente() {
            if (items == null || items.isEmpty()) {
                return tipo == TipoVeredicto.SIN_CURSADA
                        ? List.of("Sin cursada registrada")
                        : List.of("No");
            }

            List<String> mensajes = new ArrayList<>();

            // Correlativas pendientes de aprobación
            items.stream()
                    .filter(i -> !i.isAprobada())
                    .map(i -> "Pendiente: " + i.getOrden() + " (" + i.getDetalle() + ")")
                    .forEach(mensajes::add);

            // Aprobadas pero con fecha incoherente
            items.stream()
                    .filter(i -> i.isAprobada() && !i.isFechaOk())
                    .map(i -> "Fecha inválida: " + i.getOrden() + " (" + i.getDetalle() + ")")
                    .forEach(mensajes::add);

            return mensajes.isEmpty() ? List.of("No") : mensajes;
        }

        /** Compatibilidad hacia atrás con EvaluacionCorrelativaResponse */
        public EvaluacionCorrelativaResponse toLegacy() {
            String status = switch (tipo) {
                case ACEPTADA, SIN_CORRELATIVAS, FECHA_INCOHERENTE -> "Aceptada";
                case PROVISORIA                                     -> "Provisoria";
                case SIN_CURSADA                                    -> "-";
            };
            return new EvaluacionCorrelativaResponse(status, desaprobadas(), conFechaIncoherente());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // API PÚBLICA
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Evalúa correlativas de una materia para un legajo dado.
     * Reemplaza: evaluarCorrelativaIndividual()
     */
//    public Veredicto evaluar(String legajoId, Integer materiaOrden, EstadoCondicion condicion) {
//        List<NotaCursandoProjection> notas =
//                notaRepository.findNotaCursandoByLegajoAndMateria(legajoId, materiaOrden);
//        return ejecutar(notas, condicion, legajoId);
//    }

    public Veredicto evaluar(
            String legajoId,
            Integer materiaOrden,
            EstadoCondicion condicion) {

        List<NotaCursandoProjection> notas =
                notaRepository.findNotaCursandoByLegajoAndMateria(
                        legajoId,
                        materiaOrden
                );

        Materia materia = materiaRepository
                .findByMateriaOrdenAndLegajoId(materiaOrden, legajoId)
                .orElseThrow(() ->
                        new RuntimeException("Materia no encontrada"));

        return ejecutar(notas, condicion, legajoId, materia);
    }

    /**
     * Evalúa correlativas a partir de un notaId.
     * Reemplaza: evaluarCorrelativaNotaId()
     */
    public Veredicto evaluarPorNota(Long notaId, EstadoCondicion condicion) {
        List<NotaCursandoProjection> notas =
                notaRepository.findNotaNotaCursandoProjectionbyNotyaId(notaId);
        String legajoId = (notas == null || notas.isEmpty()) ? null : notas.get(0).getLegajoId();
        return ejecutar(notas, condicion, legajoId);
    }

    /**
     * Evaluación en lote: una sola query al repo para todo el legajo.
     * Reemplaza el loop en AnaliticoService — N veces más eficiente.
     * Reemplaza: obtenerTodasNotasPorLegajoAnalitico() (la parte de correlativas)
     */
    public Map<Integer, Veredicto> evaluarLote(
            String legajoId,
            List<NotaMateriaDTO> materias,
            EstadoCondicion condicion) {

        Map<String, List<NotaCursandoProjection>> notasMap = cargarNotasLegajo(legajoId);
        Map<Integer, Veredicto> resultado = new LinkedHashMap<>();

        for (NotaMateriaDTO materia : materias) {
            String orden = String.valueOf(materia.getMateriaOrden());
            List<NotaCursandoProjection> notasMateria = notasMap.getOrDefault(orden, List.of());
            resultado.put(
                    materia.getMateriaOrden(),
                    ejecutarConMapa(notasMateria, condicion, notasMap)
            );
        }
        return resultado;
    }

    /**
     * Retorna las correlativas pendientes como lista de DTOs.
     * Reemplaza: obtenerCorrelativasPendientesMateriaId()
     */

    public List<CorrelativasFaltantesEstadoDTO> pendientes(String legajoId, Materia materia) {
        String lista = materia.getMateriaCursada();
        if (esVacio(lista)) return Collections.emptyList();

        return Arrays.stream(lista.split("-"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .filter(orden -> !tieneAprobadaORegular(orden, legajoId))
                .map(orden -> {
                    int auxordmat=Integer.valueOf(orden);
                    Materia materiaAux= materiaRepository.findByMateriaOrdenAndLegajoId(auxordmat,legajoId).get();
                    CorrelativasFaltantesEstadoDTO dto = new CorrelativasFaltantesEstadoDTO();
                    dto.setMateriaNombre(materiaAux.getMateriaNombre());
                    dto.setMateriaId(materiaAux.getMateriaId());

                    dto.setMateriaOrden(orden);
                    dto.setNotaEstado(EstadoNota.LIBRE);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // MOTOR INTERNO
    // ═══════════════════════════════════════════════════════════════════════════

    private Veredicto ejecutar(
            List<NotaCursandoProjection> notasMateria,
            EstadoCondicion condicion,
            String legajoId,
            Materia materia) {

//        if (sinDatos(notasMateria)) {
//            log.warn("Sin notas para legajoId={} condicion={}", legajoId, condicion);
//            return veredictoVacio(TipoVeredicto.SIN_CURSADA);
//        }
        Map<String, List<NotaCursandoProjection>> notasMap = cargarNotasLegajo(legajoId);
        return ejecutarConMapa(
                notasMateria,
                condicion,
                notasMap,
                materia
        );
    }

    public String estadoCondicion(String legajoId, Integer materiaOrden, EstadoCondicion condicion) {
        Veredicto v = evaluar(legajoId, materiaOrden, condicion);
        return v.toLegacy().getStatus(); // "Aceptada", "Provisoria" o "-"
    }

    private Veredicto ejecutarConMapa(
            List<NotaCursandoProjection> notasMateria,
            EstadoCondicion condicion,
            Map<String, List<NotaCursandoProjection>> notasMap,
            Materia materia){

//        if (sinDatos(notasMateria)) {
//            return veredictoVacio(TipoVeredicto.SIN_CURSADA);
//        }

        NotaCursandoProjection notaOrigen =
                notasMateria.isEmpty()
                        ? null
                        : notaMasReciente(notasMateria);

        String listaRaw = resolverListaMateria(materia, condicion);
//        String listaRaw = resolverLista(notaOrigen, condicion);

        if (esVacio(listaRaw)) {
            log.debug("Sin correlativas para condicion={}", condicion);
            return veredictoVacio(TipoVeredicto.SIN_CORRELATIVAS);
        }

        boolean requiereAprobacion = requiereAprobacion(condicion);

        List<ResultadoItem> items = Arrays.stream(listaRaw.split("-"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(orden -> evaluarOrden(orden, notasMap.getOrDefault(orden, List.of()),
                        notaOrigen, requiereAprobacion))
                .collect(Collectors.toList());

        TipoVeredicto tipo = determinarTipo(items);
        log.debug("Veredicto={} para notaOrigen={} items={}", tipo,
                notaOrigen.getMateriaOrden(), items.size());

        return Veredicto.builder().tipo(tipo).items(items).build();
    }


    private String resolverListaMateria(
            Materia materia,
            EstadoCondicion condicion) {

        return switch (condicion) {
            case CURSADA ->
                    materia.getMateriaCursada();

            case EXAMEN,
                 EXAMEN_LIBRE,
                 EXAMEN_REGULAR ->
                    materia.getMateriaExamen();

            case EQUIVALENCIA -> "-";

            default -> "-";
        };
    }


    // ── Evaluación de un orden individual ────────────────────────────────────

    private ResultadoItem evaluarOrden(
            String orden,
            List<NotaCursandoProjection> candidatas,
            NotaCursandoProjection notaOrigen,
            boolean requiereAprobacion) {

        if (candidatas.isEmpty()) {
            return ResultadoItem.ausente(orden);
        }

        if (!requiereAprobacion) {
            // Condición CURSADA: alcanza con APROBADO o REGULAR
            boolean cumple = candidatas.stream().anyMatch(c ->
                    c.getNotaEstado() == EstadoNota.APROBADO
                            || c.getNotaEstado() == EstadoNota.REGULAR);
            return ResultadoItem.builder()
                    .orden(orden)
                    .aprobada(cumple)
                    .fechaOk(true)
                    .detalle(cumple ? "Regular o aprobada" : "Sin regularidad")
                    .build();
        }

        // Condición EXAMEN / por defecto: busca la mejor nota APROBADA
        for (NotaCursandoProjection c : candidatas) {
            if (c.getNotaEstado() != EstadoNota.APROBADO) continue;

            boolean fechaOk = esFechaCoherente(c, notaOrigen);
            return ResultadoItem.builder()
                    .orden(orden)
                    .aprobada(true)
                    .fechaOk(fechaOk)
                    .detalle(fechaOk ? "Ok" : "Fecha adelantada: " + c.getNotaFechaNota())
                    .fechaAprobacion(c.getNotaFechaNota())
                    .build();
        }

        return ResultadoItem.builder()
                .orden(orden)
                .aprobada(false)
                .fechaOk(false)
                .detalle("Sin aprobación registrada")
                .build();
    }

    // ── Resolución de lista y condición ──────────────────────────────────────

    private String resolverLista(NotaCursandoProjection nota, EstadoCondicion condicion) {
        return switch (condicion) {
            case CURSADA                              -> nota.getMateriaCursada();
            case EXAMEN, EXAMEN_REGULAR, EXAMEN_LIBRE -> nota.getMateriaExamen();
            case EQUIVALENCIA                         -> "-";
            default                                  -> "-";
        };
    }

    private boolean requiereAprobacion(EstadoCondicion condicion) {
        return condicion != EstadoCondicion.CURSADA;
    }

    // ── Determinación del veredicto final ────────────────────────────────────

    private TipoVeredicto determinarTipo(List<ResultadoItem> items) {
        if (items.stream().anyMatch(i -> !i.isAprobada())) return TipoVeredicto.PROVISORIA;
        if (items.stream().anyMatch(i -> !i.isFechaOk()))  return TipoVeredicto.FECHA_INCOHERENTE;
        return TipoVeredicto.ACEPTADA;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════════════════

    private Map<String, List<NotaCursandoProjection>> cargarNotasLegajo(String legajoId) {
        return notaRepository.findAllNotasByLegajo(legajoId)
                .stream()
                .collect(Collectors.groupingBy(n -> String.valueOf(n.getMateriaOrden())));
    }

    private boolean tieneAprobadaORegular(String orden, String legajoId) {
        return cursadaRepository.findByMateriaOrdenAndLegajoId(orden, legajoId)
                .stream()
                .flatMap(c -> c.getNotas().stream())
                .anyMatch(n -> n.getNotaEstado() == EstadoNota.APROBADO
                        || n.getNotaEstado() == EstadoNota.REGULAR);
    }

    private boolean esFechaCoherente(NotaCursandoProjection correlativa,
                                     NotaCursandoProjection origen) {
        if (correlativa.getNotaFechaNota() == null || origen.getNotaFechaNota() == null) {
            return false;
        }
        return !correlativa.getNotaFechaNota().isAfter(origen.getNotaFechaNota());
    }

    private NotaCursandoProjection notaMasReciente(List<NotaCursandoProjection> notas) {
        return notas.stream()
                .filter(n -> n.getNotaFechaNota() != null)
                .max(Comparator.comparing(NotaCursandoProjection::getNotaFechaNota))
                .orElseThrow(() -> new RuntimeException("Sin nota con fecha válida"));
    }

    private Veredicto veredictoVacio(TipoVeredicto tipo) {
        return Veredicto.builder().tipo(tipo).items(List.of()).build();
    }

    private boolean esVacio(String s) {
        return s == null || s.isBlank() || s.equals("-");
    }

    private boolean sinDatos(List<NotaCursandoProjection> notas) {
        return notas == null || notas.isEmpty();
    }
}