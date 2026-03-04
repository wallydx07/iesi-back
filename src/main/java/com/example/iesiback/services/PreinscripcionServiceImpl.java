package com.example.iesiback.services;

import com.example.iesiback.entities.Preinscripcion;
import com.example.iesiback.repositories.PreinscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PreinscripcionServiceImpl implements PreinscripcionService {

    @Autowired
    private PreinscripcionRepository preinscripcionRepository;

    @Autowired
    private EmailService emailService;


    @Override
    public List<Preinscripcion> obtenerPreinscripcion() {
       // return preinscripcionRepository.findAll();
        return preinscripcionRepository.findAll(Sort.by(Sort.Direction.ASC, "fecha"));
    }

    @Override
    public Optional<Preinscripcion> obtenerPreinscripcionPorId(int id) {
        return preinscripcionRepository.findById(id);
    }

    @Override
    public Preinscripcion guardarPreinscripcion(Preinscripcion preinscripcion) {
        preinscripcion.setFecha(Instant.now());
        Preinscripcion guardada = preinscripcionRepository.save(preinscripcion);

        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("codigo", guardada.getId());
            variables.put("nombre", guardada.getNombre());
            variables.put("apellido", guardada.getApellido());
            variables.put("dni", guardada.getDni());
            variables.put("carrera", guardada.getCarreraSolicitada());
            variables.put("turno", guardada.getTurno());
            variables.put("fecha", guardada.getFecha());
            emailService.enviarCorreoConPlantilla(
                    guardada.getEmail(),
                    "Confirmación de Preinscripción",
                    "preinscripcion-confirmacion", // nombre del HTML
                    variables
            );
        } catch (Exception e) {
            // IMPORTANTE: no romper el flujo de guardado
            e.printStackTrace();
        }
        return guardada;
    }

    @Override
    public Preinscripcion actualizarPreinscripcion(Preinscripcion preinscripcion) {
        preinscripcion.setFecha(Instant.now());
        Preinscripcion guardada = preinscripcionRepository.save(preinscripcion);

        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("codigo", guardada.getId());
            variables.put("nombre", guardada.getNombre());
            variables.put("apellido", guardada.getApellido());
            variables.put("dni", guardada.getDni());
            variables.put("carrera", guardada.getCarreraSolicitada());
            variables.put("turno", guardada.getTurno());
            variables.put("fecha", guardada.getFecha());
        } catch (Exception e) {
            // IMPORTANTE: no romper el flujo de guardado
            e.printStackTrace();
        }
        return guardada;
    }



    @Override
    public boolean eliminarPreinscripcion(int id) {
        if (preinscripcionRepository.existsById(id)) {
            preinscripcionRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
