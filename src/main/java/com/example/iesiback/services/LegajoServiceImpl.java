package com.example.iesiback.services;

import com.example.iesiback.entities.Persona;
import com.example.iesiback.entities.Carrera;
import com.example.iesiback.entities.Legajo;
import com.example.iesiback.repositories.LegajoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LegajoServiceImpl implements LegajoService {

    private final LegajoRepository legajoRepository;

    @Autowired
    public LegajoServiceImpl(LegajoRepository legajoRepository) {
        this.legajoRepository = legajoRepository;
    }

    @Override
    public Legajo findLegajoById(String id) {
        return legajoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Legajo no encontrado con ID: " + id));
    }

    @Override
    public List<Legajo> obtenerLegajos() {
        return legajoRepository.findAll();
    }

    @Override
    public Optional<Legajo> findById(String id) {
        return legajoRepository.findById(id);
    }

    @Override
    public Legajo guardarLegajo(Legajo legajo, Carrera carrera) {
        System.out.println("--------------------carrera id:"+carrera.getCarreraId());
        String prefijo = carrera.getCarreraId().split("-")[0];
        legajo.setLegajoId(this.generaLegajo(prefijo));
        return legajoRepository.save(legajo);
    }

    @Override
    public Legajo updateLegajo(Legajo legajo) {
        if (legajoRepository.existsById(legajo.getLegajoId())) {
            return legajoRepository.save(legajo);  // Actualiza el legajo
        } else {
            throw new EntityNotFoundException("Legajo no encontrado");
        }
    }

    @Override
    public String generaLegajo(String prefijo) {
        System.out.println("---------------------------------------------"+prefijo);
        String maxLegajo = legajoRepository.findMaxLegajoId(prefijo);
        String numeroStr = maxLegajo.replaceAll("\\D+", ""); // Solo deja los dígitos
        int numero = Integer.parseInt(numeroStr) + 1; // Incrementa el número

        return prefijo + numero;
    }

    @Transactional
    public void actualizarAlumnoDNI(Persona personaViejo, Persona nuevoPersona) {
        List<Legajo> legajos = legajoRepository.findByLegajoPersonaDni(personaViejo);
        for (Legajo l : legajos) {
            l.setLegajoPersonaDni(nuevoPersona);
        }
        legajoRepository.saveAll(legajos);
    }


}
