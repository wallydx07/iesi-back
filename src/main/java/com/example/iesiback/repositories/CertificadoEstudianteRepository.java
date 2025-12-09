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
    List<CertificadoEstudiante> findByAtencion_LegajoId(String legajoId);

//    @Query("SELECT new com.example.iesiback.dto.AporteDTO(" +
//            "c.id, " +
//            "al.personaDni, " +
//            "al.personaApellido, " +
//            "al.personaNombre, " +
//            "a.legajoId, " +
//            "c.monto, " +
//            "0, " +                    // recibo (falso, 0)
//            "0, " +                    // talonario (falso, 0)
//            "c.fecha, " +
//            "c.observaciones, " +
//            "c.usuario, " +
//            "c.validado) " +
//            "FROM CertificadoEstudiante c " +
//            "JOIN c.atencion a " +
//            "JOIN a.atencionDni al")
//    List<AporteDTO> findCertificadosComoAportes();
//

    @Query(value = """
    SELECT
      c.constancia_id,
      p.persona_apellido,
      p.persona_nombre,
      a.legajo_id,
      c.monto,
      0 AS recibo,
      0 AS talonario,
      c.fecha,
      c.observaciones,
      c.usuario,
      c.validado
    FROM certificado_estudiante c
    INNER JOIN atencion a ON c.atencion_id = a.atencion_id
    INNER JOIN persona p ON a.atencion_dni = p.persona_dni
    ORDER BY c.fecha DESC
""", nativeQuery = true)
    List<Object[]> findCertificadosComoAportes();


}