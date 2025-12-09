package com.example.iesiback.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service  // 🔥 Muy importante para que Spring lo registre como bean
public class NotificacionService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void enviarNotificacion(Long userId, String mensaje) {
        messagingTemplate.convertAndSend("/topic/notificacion/" + userId, mensaje);
    }
}
