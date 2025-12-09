package com.example.iesiback.services;

import com.example.iesiback.entities.Persona;
import com.example.iesiback.repositories.PersonaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PersonaServiceImpl implements PersonaService {

    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private LegajoService legajoService;
    @Override
    public Persona createAlumno(Persona persona) {
        return personaRepository.save(persona);
    }

    @Override
    public Persona findAlumnoById(String id) {
        return personaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con ID: " + id));
    }


@Override
public Persona obtenerAlumnoPorLegajoId(String legajoId) {
        return personaRepository.findAlumnoByLegajoId(legajoId);
    }

    @Override
    public List<Persona> obtenerAlumnos() {
        return personaRepository.findAll();
    }

    @Override
    public Optional<Persona> findById(String id) {
        return personaRepository.findById(id);
    }

    @Override
    public Persona save(Persona persona) {
        return personaRepository.save(persona);
    }

    @Override
    public boolean delete(String id) {
        return personaRepository.findById(id).map(alumno -> {
            personaRepository.delete(alumno);
            return true;
        }).orElse(false);
    }

    @Override
    public List<String> buscarPorDniApellidoNombre(String busqueda) {
        if (busqueda == null || busqueda.trim().isEmpty()) {
            return personaRepository.buscarPorDniApellidoNombre(null);
        }
        // Armamos patrón de búsqueda con múltiples palabras
        String[] palabras = busqueda.trim().split("\\s+");
        String busquedaParam = "%" + String.join("%", palabras) + "%"; // Ej: %juan%perez%

        return personaRepository.buscarPorDniApellidoNombre(busquedaParam.toLowerCase());
    }



    @Override
    public List<Persona> buscarPorDni(String dni) {
        return personaRepository.buscarPorDni(dni);
    }



    @Override
    public List<String> buscarPorApellidoYCarrera(String busqueda, String carreraNombre) {
        String busquedaParam = null;
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            String[] palabras = busqueda.trim().split("\\s+");
            busquedaParam = "%" + String.join("%", palabras) + "%";
        }
        return personaRepository.buscarPorApellidoYCarrera(
                busquedaParam != null ? busquedaParam.toLowerCase() : null,
                carreraNombre != null && !carreraNombre.trim().isEmpty() ? carreraNombre.toLowerCase() : null
        );
    }

    @Transactional
    public void cambioDNI(String dniActual, Long dniCorrecto) {
        Persona personaViejo = buscarPorDni(dniActual)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        // Crear nuevo alumno
        Persona nuevoPersona = new Persona();
        BeanUtils.copyProperties(personaViejo, nuevoPersona, "alumnoDni", "id");
        nuevoPersona.setPersonaDni(dniCorrecto);
        personaRepository.save(nuevoPersona);

        // Actualizar legajos
        legajoService.actualizarAlumnoDNI(personaViejo, nuevoPersona);

        // Eliminar alumno viejo
        personaRepository.delete(personaViejo);
    }


//@Override
//public List<AlumnoExamenDTO> AlumnoExamenDTO(String apellido, String carreraNombre) {
//        return alumnoRepository.buscarAlumnos(apellido, carreraNombre);
//    }
}
