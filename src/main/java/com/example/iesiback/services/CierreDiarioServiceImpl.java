package com.example.iesiback.services;

import com.example.iesiback.dto.ReciboDTO;
import com.example.iesiback.dto.ResumenOperadorDTO;
import com.example.iesiback.entities.CierreDiario;
import com.example.iesiback.entities.Pago;
import com.example.iesiback.entities.User;
import com.example.iesiback.enums.EstadoCierre;
import com.example.iesiback.enums.EstadoPago;
import com.example.iesiback.repositories.CierreDiarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CierreDiarioServiceImpl implements CierreDiarioService {

    private final CierreDiarioRepository cierreRepo;
    private final UserService userService;
    private final PagoService pagoService;

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
        // Buscamos si ya existe el registro para la fecha indicada
        CierreDiario cierre = cierreRepo.findByFechaAndUsuarioId(fecha, usuarioId)
                .orElse(new CierreDiario(fecha, EstadoCierre.ABIERTO, usuarioId));
        if (cierre.getEstado() == EstadoCierre.CERRADO) {
            throw new RuntimeException("El día ya fue cerrado por este usuario");
        }
        if (cierre.getEstado() == EstadoCierre.AUDITADO) {
            throw new RuntimeException("No se puede cerrar un día que ya fue auditado");
        }
        cierre.setEstado(EstadoCierre.CERRADO);
        cierre.setFechaCierre(LocalDateTime.now()); // Marca de tiempo real de la ejecución
        cierreRepo.save(cierre);
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
                pagoService.obtenerResumenPorOperador(fecha, user);
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
}