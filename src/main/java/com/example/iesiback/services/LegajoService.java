package com.example.iesiback.services;

import com.example.iesiback.entities.Persona;
import com.example.iesiback.entities.Carrera;
import com.example.iesiback.entities.Legajo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
public interface LegajoService {
    Legajo findLegajoById(String id);
    List<Legajo> obtenerLegajos();
    Optional<Legajo> findById(String id);
    Legajo updateLegajo(Legajo legajo);
    String generaLegajo(String prefijo);


    @Transactional
    void actualizarAlumnoDNI(Persona personaViejo, Persona nuevoPersona);

    Legajo findByPersonaAndCarreraId(Long personaDni, String carreraId);

    @Transactional
    Legajo findOrCreateLegajo(Persona persona, String carreraId);

    Legajo crearLegajoDesdeRequest(Map<String, Object> request);

    List<Legajo> findLegajosByDNI(String dni);

}
