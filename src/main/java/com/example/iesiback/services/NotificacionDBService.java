package com.example.iesiback.services;
import com.example.iesiback.entities.Notificacion;
import com.example.iesiback.repositories.NotificacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionDBService {

    private final NotificacionRepository repository;

    public NotificacionDBService(NotificacionRepository repository) {
        this.repository = repository;
    }

    public Notificacion guardar(Notificacion notificacion) {
        return repository.save(notificacion);
    }

    public List<Notificacion> pendientes(Long usuarioId) {
        return repository.findByUsuarioIdAndLeidoFalse(usuarioId);
    }

    public void marcarComoLeido(Long usuarioId) {
        List<Notificacion> pendientes = pendientes(usuarioId);
        pendientes.forEach(n -> n.setLeido(true));
        repository.saveAll(pendientes);
    }
}
