package com.example.iesiback.services;

import com.example.iesiback.entities.CertificadoEstudiante;
import com.example.iesiback.repositories.CertificadoEstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            return repository.findByAtencionId(atencionId);
        }

        @Override
        public List<CertificadoEstudiante> findByLegajoId(String legajoId) {
            return repository.findByLegajo_LegajoId(legajoId);
        }
    }
