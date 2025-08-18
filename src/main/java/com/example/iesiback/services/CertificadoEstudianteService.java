package com.example.iesiback.services;

import com.example.iesiback.dto.AporteDTO;
import com.example.iesiback.entities.CertificadoEstudiante;

import java.util.List;

public interface CertificadoEstudianteService {

    List<CertificadoEstudiante> findAll();
    CertificadoEstudiante findById(Integer id);
    CertificadoEstudiante save(CertificadoEstudiante constancia);
    void deleteById(Integer id);
    List<CertificadoEstudiante> findByTipo(String tipo);
    List<CertificadoEstudiante> findByAutoridad(String autoridad);
    List<CertificadoEstudiante> findByEstado(String estado);
    List<CertificadoEstudiante> findByUsuario(String usuario);
    List<CertificadoEstudiante> findByValidado(Boolean validado);
    List<CertificadoEstudiante> findByMonto(Integer monto);
    List<CertificadoEstudiante> findByAtencionId(Integer atencionId);
    List<CertificadoEstudiante> findByLegajoId(String legajoId);

    List<CertificadoEstudiante> saveAll(List<CertificadoEstudiante> certificados);

    List<AporteDTO> obtenerCertificadosComoAportes();
}