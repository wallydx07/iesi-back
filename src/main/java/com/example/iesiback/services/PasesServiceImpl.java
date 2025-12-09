package com.example.iesiback.services;

import com.example.iesiback.entities.Notificacion;
import com.example.iesiback.entities.Pases;
import com.example.iesiback.repositories.PasesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PasesServiceImpl implements PasesService {

    private final PasesRepository pasesRepository;

    @Autowired
    UserService userService;

    // Inyectamos el servicio
    @Autowired
    private NotificacionService notificationService;

    @Autowired
    private NotificacionDBService notificacionDBService;


    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    PersonalService personalService;

    public PasesServiceImpl(PasesRepository pasesRepository) {
        this.pasesRepository = pasesRepository;
    }

    @Override
    public List<Pases> findAll() {
        return pasesRepository.findAll();
    }

    @Override
    public Pases findById(Long id) {
        return pasesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pase no encontrado con ID: " + id));
    }

    @Override
    public List<Pases> findByTramiteId(Long tramiteId) {
        return pasesRepository.findByTramiteId(tramiteId);
    }

    @Override
    public List<Pases> findByUsuarioOrigen(Long usuarioId) {
        return pasesRepository.findByDeUsuarioId(usuarioId);
    }

    @Override
    public List<Pases> findByUsuarioDestino(Long usuarioId) {
        return pasesRepository.findByParaUsuarioId(usuarioId);
    }

    @Override
    public Pases save(Pases pase) {
        String id=userService.getAuthenticatedUser().get().getUsername();
        pase.setDeUsuario(personalService.findById(id).get());

        if (pase.getParaUsuario() != null) {
            Long userId = pase.getParaUsuario().getId();
            String mensaje = "Nuevo pase para vos";

            Notificacion notificacion = new Notificacion(userId, mensaje);
            notificacionDBService.guardar(notificacion);

            notificationService.enviarNotificacion(userId, mensaje);
            System.out.println("📩 Notificación enviada a usuarioId=" + userId + ": " + mensaje);

        }


        return pasesRepository.save(pase);
    }
    // ✅ Implementación del método para destino (área)
    @Override
    public List<Pases> getByParaDestino(Long destinoId) {
        return pasesRepository.findByParaDestinoId(destinoId);
    }
}
