package com.example.iesiback.services;

import com.example.iesiback.entities.Atencion;
import com.example.iesiback.repositories.AtencionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AtencionServiceImpl implements AtencionService {

    private final AtencionRepository repository;

    public AtencionServiceImpl(AtencionRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Atencion> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Atencion> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public Atencion save(Atencion atencion) {
        return repository.save(atencion);
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public List<Atencion> findByDni(Long dni) {
        return repository.findByAtencionDni(dni);
    }

    @Override
    public List<Atencion> findByApellidoNombre(String apellidoNombre) {
        return repository.findByAtencionApellidoNombreContainingIgnoreCase(apellidoNombre);
    }

    @Override
    public List<Atencion> findByCorreo(String correo) {
        return repository.findByAtencionCorreoContainingIgnoreCase(correo);
    }

    @Override
    public List<Atencion> findByDestino(String destino) {
        return repository.findByAtencionDestino(destino);
    }

    @Override
    public List<Atencion> findByUsuario(String usuario) {
        return repository.findByAtencionUsuario(usuario);
    }

    @Override
    public List<Atencion> findByResuelto(Boolean resuelto) {
        return repository.findByAtencionResuelto(resuelto);
    }

    @Override
    public List<Atencion> findByFecha(LocalDate fecha) {
        return repository.findByAtencionFecha(fecha);
    }
}