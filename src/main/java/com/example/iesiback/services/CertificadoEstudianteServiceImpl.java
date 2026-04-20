package com.example.iesiback.services;

import com.example.iesiback.dto.AporteDTO;
import com.example.iesiback.entities.CertificadoEstudiante;
import com.example.iesiback.repositories.CertificadoEstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
    public class CertificadoEstudianteServiceImpl implements CertificadoEstudianteService {

        @Autowired
        private CertificadoEstudianteRepository repository;

        @Override
        public List<CertificadoEstudiante> findAll() {
            return repository.findAll();
        }

        @Override
        public CertificadoEstudiante findById(Integer id) {
            return repository.findById(id).orElse(null);
        }

        @Override
        public CertificadoEstudiante save(CertificadoEstudiante constancia) {
            return repository.save(constancia);
        }

        @Override
        public void deleteById(Integer id) {
            repository.deleteById(id);
        }

        @Override
        public List<CertificadoEstudiante> findByTipo(String tipo) {
            return repository.findByTipoContainingIgnoreCase(tipo);
        }

        @Override
        public List<CertificadoEstudiante> findByAutoridad(String autoridad) {
            return repository.findByAutoridadContainingIgnoreCase(autoridad);
        }

        @Override
        public List<CertificadoEstudiante> findByEstado(String estado) {
            return repository.findByEstadoContainingIgnoreCase(estado);
        }

        @Override
        public List<CertificadoEstudiante> findByUsuario(String usuario) {
            return repository.findByUsuarioContainingIgnoreCase(usuario);
        }

        @Override
        public List<CertificadoEstudiante> findByValidado(Boolean validado) {
            return repository.findByValidado(validado);
        }

        @Override
        public List<CertificadoEstudiante> findByMonto(Integer monto) {
            return repository.findByMonto(monto);
        }

        @Override
        public List<CertificadoEstudiante> findByAtencionId(Integer atencionId) {
            return repository.findByTramiteId(atencionId);
        }

        @Override
        public List<CertificadoEstudiante> findByLegajoId(String legajoId) {
            return repository.findByTramite_LegajoId(legajoId);
        }

    @Override
    public List<CertificadoEstudiante> saveAll(List<CertificadoEstudiante> certificados) {
        return repository.saveAll(certificados);
    }

    @Override
    public List<AporteDTO> obtenerCertificadosComoAportes() {
        List<AporteDTO> lista = repository.findCertificadosComoAportes()
                .stream()
                .map(obj -> new AporteDTO(
                        ((Number)obj[0]).intValue(),
                        null, // DNI si querés agregarlo después
                        (String)obj[1],
                        (String)obj[2],
                        ((String)obj[3]),
                        ((Number)obj[4]).intValue(),
                        ((Number)obj[5]).longValue(),
                        ((Number)obj[6]).intValue(),
                        LocalDate.parse(obj[7].toString()),
                        (String)obj[8],
                        (String)obj[9],
                        (Boolean)obj[10]
                ))
                .toList();
        return lista;
    }
}
