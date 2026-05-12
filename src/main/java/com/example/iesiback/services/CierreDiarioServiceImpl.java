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

    // 🔵 CERRAR DÍA POR USUARIO
    @Override
    public void cerrarDia(String usuarioId) {

        LocalDate hoy = LocalDate.now();

        CierreDiario cierre = cierreRepo.findByFechaAndUsuarioId(hoy, usuarioId)
                .orElse(new CierreDiario(hoy, EstadoCierre.ABIERTO, usuarioId));

        if (cierre.getEstado() == EstadoCierre.CERRADO) {
            throw new RuntimeException("El día ya fue cerrado por este usuario");
        }

        cierre.setEstado(EstadoCierre.CERRADO);
        cierre.setFechaCierre(LocalDateTime.now());

        cierreRepo.save(cierre);
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

    // 🧾 AUDITAR DÍA (POR USUARIO)
    @Override
    public void auditarDia(String usuarioId) {

        LocalDate hoy = LocalDate.now();

        CierreDiario cierre = cierreRepo.findByFechaAndUsuarioId(hoy, usuarioId)
                .orElseThrow(() -> new RuntimeException("No existe cierre para este usuario hoy"));

        if (cierre.getEstado() != EstadoCierre.CERRADO) {
            throw new RuntimeException("Solo se puede auditar un día cerrado");
        }

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