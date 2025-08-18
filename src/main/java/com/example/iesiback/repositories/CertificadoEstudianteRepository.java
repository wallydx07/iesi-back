package com.example.iesiback.repositories;

import com.example.iesiback.dto.AporteDTO;
import com.example.iesiback.entities.CertificadoEstudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificadoEstudianteRepository extends JpaRepository<CertificadoEstudiante, Integer> {

    List<CertificadoEstudiante> findByTipoContainingIgnoreCase(String tipo);
    List<CertificadoEstudiante> findByAutoridadContainingIgnoreCase(String autoridad);
    List<CertificadoEstudiante> findByEstadoContainingIgnoreCase(String estado);
    List<CertificadoEstudiante> findByUsuarioContainingIgnoreCase(String usuario);
    List<CertificadoEstudiante> findByValidado(Boolean validado);
    List<CertificadoEstudiante> findByMonto(Integer monto);
    List<CertificadoEstudiante> findByAtencionId(Integer atencionId);
    List<CertificadoEstudiante> findByAtencion_Legajo_LegajoId(String legajoId);

    @Query("SELECT new com.example.iesiback.dto.AporteDTO(" +
            "c.id, " +
            "al.alumnoDni, " +
            "al.alumnoApellido, " +
            "al.alumnoNombre, " +
            "a.legajo.legajoId, " +
            "c.monto, " +
            "0, " +                    // recibo (falso, 0)
            "0, " +                    // talonario (falso, 0)
            "c.fecha, " +
            "c.observaciones, " +
            "c.usuario, " +
            "c.validado) " +
            "FROM CertificadoEstudiante c " +
            "JOIN c.atencion a " +
            "JOIN a.legajo.legajoAlumnoDni al")
    List<AporteDTO> findCertificadosComoAportes();



}