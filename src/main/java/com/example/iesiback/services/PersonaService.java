package com.example.iesiback.services;

import com.example.iesiback.dto.PersonaDTO;
import com.example.iesiback.dto.PromedioEgresadoDTO;
import com.example.iesiback.entities.Persona;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PersonaService {
    Persona findAlumnoById(String id);
    Persona obtenerAlumnoPorLegajoId(String legajoId);
    List<Persona> obtenerAlumnos();
    Optional<Persona> findById(String id);
    Persona save(Persona persona);
    boolean delete(String id);

    List<String> buscarPorDniApellidoNombre(String busqueda);

    List<String> buscarPersonalPorDniApellidoNombre(String busqueda);

    List<Persona> buscarPorDni(String dni);
    Persona createAlumno(Persona persona);
    List<String> buscarPorApellidoYCarrera(String apellido, String carreraNombre);

    @Transactional
    void cambioDNI(String dniActual, Long dniCorrecto);

    List<PromedioEgresadoDTO> obtenerEgresados(Integer year);

    PersonaDTO findPersonaDTOById(Long id);

//    List<AlumnoExamenDTO> AlumnoExamenDTO(String apellido, String carreraNombre);



}
