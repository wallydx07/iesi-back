package com.example.iesiback.services;

import com.example.iesiback.entities.CierreDiario;
import com.example.iesiback.entities.User;
import com.example.iesiback.enums.EstadoCierre;
import com.example.iesiback.repositories.CierreDiarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class CierreDiarioServiceImpl implements CierreDiarioService {

    private final CierreDiarioRepository cierreRepo;
    private final UserService userService;

    public CierreDiarioServiceImpl(CierreDiarioRepository cierreRepo, UserService userService) {
        this.cierreRepo = cierreRepo;
        this.userService = userService;
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

//    // 🔵 CERRAR DÍA POR USUARIO
//    @Override
//    public void cerrarDia(String usuarioId) {
//
//        LocalDate hoy = LocalDate.now();
//
//        CierreDiario cierre = cierreRepo.findByFechaAndUsuarioId(hoy, usuarioId)
//                .orElse(new CierreDiario(hoy, EstadoCierre.ABIERTO, usuarioId));
//
//        if (cierre.getEstado() == EstadoCierre.CERRADO) {
//            throw new RuntimeException("El día ya fue cerrado por este usuario");
//        }
//
//        cierre.setEstado(EstadoCierre.CERRADO);
//        cierre.setFechaCierre(LocalDateTime.now());
//
//        cierreRepo.save(cierre);
//    }
//
//
//    // 🧾 AUDITAR DÍA (POR USUARIO)
//    @Override
//    public void auditarDia(String usuarioId) {
//        LocalDate hoy = LocalDate.now();
//
//        // 1. Buscamos si ya existe el registro de hoy
//        CierreDiario cierre = cierreRepo.findByFechaAndUsuarioId(hoy, usuarioId)
//                .orElseGet(() -> {
//                    // Si no existe, creamos uno nuevo directamente en estado CERRADO (ahorramos pasos)
//                    CierreDiario nuevoCierre = new CierreDiario(hoy, EstadoCierre.CERRADO, usuarioId);
//                    nuevoCierre.setFechaCierre(LocalDateTime.now());
//                    return nuevoCierre;
//                });
//
//        // 2. Si existía pero estaba ABIERTO, lo forzamos a CERRADO antes de auditar
//        if (cierre.getEstado() == EstadoCierre.ABIERTO) {
//            cierre.setEstado(EstadoCierre.CERRADO);
//            cierre.setFechaCierre(LocalDateTime.now());
//        }
//
//        // 3. Ahora que estamos seguros de que está (o estuvo) CERRADO, auditamos
//        cierre.setEstado(EstadoCierre.AUDITADO);
//        // Nota: Puedes mantener 'fechaCierre' o usar un campo específico como 'fechaAuditoria' si lo tienes
//        cierre.setFechaCierre(LocalDateTime.now());
//
//        cierreRepo.save(cierre);
//    }


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

        // 1. Buscamos si ya existe el registro para la fecha indicada
        CierreDiario cierre = cierreRepo.findByFechaAndUsuarioId(fecha, usuarioId)
                .orElseGet(() -> {
                    // Si no existe, creamos uno nuevo directamente simulando el paso de CERRADO
                    CierreDiario nuevoCierre = new CierreDiario(fecha, EstadoCierre.CERRADO, usuarioId);
                    nuevoCierre.setFechaCierre(LocalDateTime.now());
                    return nuevoCierre;
                });

        // 2. Si existía pero estaba ABIERTO, lo forzamos a CERRADO antes de auditar
        if (cierre.getEstado() == EstadoCierre.ABIERTO) {
            cierre.setEstado(EstadoCierre.CERRADO);
            cierre.setFechaCierre(LocalDateTime.now());
        }

        // 3. Ahora que pasó las validaciones de cierre, auditamos
        cierre.setEstado(EstadoCierre.AUDITADO);
        cierre.setFechaCierre(LocalDateTime.now());

        cierreRepo.save(cierre);
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