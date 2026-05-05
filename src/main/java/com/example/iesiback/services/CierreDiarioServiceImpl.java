package com.example.iesiback.services;

import com.example.iesiback.entities.CierreDiario;
import com.example.iesiback.enums.EstadoCierre;
import com.example.iesiback.repositories.CierreDiarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class CierreDiarioServiceImpl implements CierreDiarioService {

    private final CierreDiarioRepository cierreRepo;

    public CierreDiarioServiceImpl(CierreDiarioRepository cierreRepo) {
        this.cierreRepo = cierreRepo;
    }

    @Override
    public void cerrarDia(Long usuarioId) {
        LocalDate hoy = LocalDate.now();

        CierreDiario cierre = cierreRepo.findByFecha(hoy)
                .orElse(new CierreDiario(hoy, EstadoCierre.ABIERTO));

        if (cierre.getEstado() == EstadoCierre.CERRADO) {
            throw new RuntimeException("El día ya fue cerrado");
        }

        cierre.setEstado(EstadoCierre.CERRADO);
        cierre.setFechaCierre(LocalDateTime.now());
        cierre.setUsuarioId(usuarioId);

        cierreRepo.save(cierre);
    }

    @Override
    public void validarDiaAbierto() {
        LocalDate hoy = LocalDate.now();

        CierreDiario cierre = cierreRepo.findByFecha(hoy).orElse(null);

        if (cierre != null && cierre.getEstado() != EstadoCierre.ABIERTO) {
            throw new RuntimeException("El día está cerrado, no se pueden registrar cobros");
        }
    }

    @Override
    public boolean estaCerradoHoy() {
        LocalDate hoy = LocalDate.now();

        return cierreRepo.findByFecha(hoy)
                .map(c -> c.getEstado() != EstadoCierre.ABIERTO)
                .orElse(false);
    }

    /**
     * 🧾 AUDITAR DÍA
     */
    @Override
    public void auditarDia(Long usuarioId) {
        LocalDate hoy = LocalDate.now();

        CierreDiario cierre = cierreRepo.findByFecha(hoy)
                .orElseThrow(() -> new RuntimeException("No existe cierre para hoy"));

        if (cierre.getEstado() != EstadoCierre.CERRADO) {
            throw new RuntimeException("Solo se puede auditar un día cerrado");
        }

        cierre.setEstado(EstadoCierre.AUDITADO);
        cierre.setUsuarioId(usuarioId);

        cierreRepo.save(cierre);
    }
}