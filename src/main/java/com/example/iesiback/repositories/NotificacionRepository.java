package com.example.iesiback.repositories;

import com.example.iesiback.entities.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioIdAndLeidoFalse(Long usuarioId);
}