package com.example.iesiback.services;

import com.example.iesiback.dto.ReciboDTO;
import com.example.iesiback.dto.ResumenOperadorDTO;
import com.example.iesiback.entities.CierreDiario;
import com.example.iesiback.entities.Pago;
import com.example.iesiback.entities.User;
import com.example.iesiback.enums.EstadoCierre;
import com.example.iesiback.enums.EstadoPago;
import com.example.iesiback.repositories.CierreDiarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CierreDiarioServiceImpl implements CierreDiarioService {

    private final CierreDiarioRepository cierreRepo;
    private final UserService userService;
    private final PagoService pagoService;

    // arriba en la clase:
    private static final Logger log = LoggerFactory.getLogger(CierreDiarioServiceImpl.class);



    public CierreDiarioServiceImpl(CierreDiarioRepository cierreRepo, UserService userService, PagoService pagoService) {
        this.cierreRepo = cierreRepo;
        this.userService = userService;
        this.pagoService = pagoService;
    }


    // 🟢 VALIDAR SI EL USUARIO PUEDE OPERAR HOY
    @Override
    public void validarDiaAbierto() {

        User user = userService.getAuthenticatedUser()
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

        String usuarioId = user.getUsername();
        LocalDate hoy = LocalDate.now();

        CierreDiario cierre = cierreRepo.findByFechaAndUsuarioId(hoy, usuarioId)
                .orElse(null);

        if (cierre != null && cierre.getEstado() != EstadoCierre.ABIERTO) {
            throw new RuntimeException("Tu día está cerrado, no puedes registrar operaciones");
        }
    }

    // 🔵 VER SI ESTÁ CERRADO HOY
    @Override
    public boolean estaCerradoHoy() {

        User user = userService.getAuthenticatedUser()
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

        String usuarioId = user.getUsername();
        LocalDate hoy = LocalDate.now();

        return cierreRepo.findByFechaAndUsuarioId(hoy, usuarioId)
                .map(c -> c.getEstado() != EstadoCierre.ABIERTO)
                .orElse(false);
    }



    // 🔵 CERRAR DÍA POR USUARIO
    @Override
    public void cerrarDia(String usuarioId, LocalDate fecha) {
        log.info("Cerrando día {} para usuario {}", fecha, usuarioId);

        CierreDiario cierre = cierreRepo.findByFechaAndUsuarioId(fecha, usuarioId)
                .orElse(new CierreDiario(fecha, EstadoCierre.ABIERTO, usuarioId));

        log.info("Estado actual del cierre: {}", cierre.getEstado());

        if (cierre.getEstado() == EstadoCierre.CERRADO) {
            log.warn("Intento de cerrar día ya cerrado. Usuario: {}, fecha: {}", usuarioId, fecha);
            throw new RuntimeException("El día ya fue cerrado por este usuario");
        }
        if (cierre.getEstado() == EstadoCierre.AUDITADO) {
            log.warn("Intento de cerrar día auditado. Usuario: {}, fecha: {}", usuarioId, fecha);
            throw new RuntimeException("No se puede cerrar un día que ya fue auditado");
        }

        try {
            cierre.setEstado(EstadoCierre.CERRADO);
            cierre.setFechaCierre(LocalDateTime.now());
            cierreRepo.save(cierre);
            log.info("Día {} cerrado correctamente por usuario {}", fecha, usuarioId);
        } catch (Exception e) {
            log.error("Error al guardar el cierre. Usuario: {}, fecha: {}", usuarioId, fecha, e);
            throw e;
        }
    }


    // 🧾 AUDITAR DÍA (POR USUARIO)
    @Override
    public void auditarDia(String usuarioId, LocalDate fecha) {
        CierreDiario cierre = cierreRepo.findByFechaAndUsuarioId(fecha, usuarioId)
                .orElseGet(() -> {
                    CierreDiario nuevoCierre = new CierreDiario(fecha, EstadoCierre.CERRADO, usuarioId);
                    nuevoCierre.setFechaCierre(LocalDateTime.now());
                    return nuevoCierre;
                });
        if (cierre.getEstado() == EstadoCierre.ABIERTO) {
            cierre.setEstado(EstadoCierre.CERRADO);
            cierre.setFechaCierre(LocalDateTime.now());
        }
        cierre.setEstado(EstadoCierre.AUDITADO);
        cierre.setFechaCierre(LocalDateTime.now());
        cierreRepo.save(cierre);
       auditarDiaPago(usuarioId, fecha);
    }


    public void auditarDiaPago(String usuarioId, LocalDate fecha) {
        long id = Long.parseLong(usuarioId);
        User user = userService.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));
        List<ResumenOperadorDTO> lista =
                pagoService.obtenerResumenPorOperador(fecha,fecha, user);
        for (ResumenOperadorDTO item : lista) {
            for (ReciboDTO recibo : item.getItems()) {
                if (EstadoPago.PENDIENTE.equals(recibo.getEstado())) {

                    Pago pago = pagoService.buscarPorId(recibo.getAporteId().intValue())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Pago no encontrado ID: "
                                                    + recibo.getAporteId()
                                    ));
                    pago.setEstado(EstadoPago.APROBADO);
                    pagoService.guardar(pago);
                }
            }
        }
    }


    // 🟡 ESTADO DEL DÍA DEL USUARIO LOGUEADO
    @Override
    public String estadoHoy() {
        User user = userService.getAuthenticatedUser()
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));
        String usuarioId = user.getUsername();
        LocalDate hoy = LocalDate.now();
        return cierreRepo.findByFechaAndUsuarioId(hoy, usuarioId)
                .map(c -> c.getEstado().name())
                .orElse("ABIERTO");
    }

    // 🟡 CIERRE COMPLETO DEL DÍA (o de una fecha dada) DEL USUARIO LOGUEADO
    @Override
    public CierreDiario cierrePorFecha(String usuarioId, LocalDate fecha) {
        LocalDate fechaBuscada = (fecha != null) ? fecha : LocalDate.now();

        return cierreRepo.findByFechaAndUsuarioId(fechaBuscada, usuarioId)
                .orElseGet(() -> new CierreDiario(fechaBuscada, EstadoCierre.ABIERTO, usuarioId));
    }

}