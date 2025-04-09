package com.example.iesiback.repositories;

import com.example.iesiback.entities.CertificadoEstudiante;
import org.springframework.data.jpa.repository.JpaRepository;
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
    List<CertificadoEstudiante> findByLegajo_LegajoId(String legajoId); // O ajustá al campo de Legajo que uses como ID
}