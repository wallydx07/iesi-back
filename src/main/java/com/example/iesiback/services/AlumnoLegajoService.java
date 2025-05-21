package com.example.iesiback.services;

import java.util.List;
import com.example.iesiback.dto.AlumnoLegajoInscripcionCarreraDTO;
import com.example.iesiback.repositories.AlumnoLegajoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlumnoLegajoService {

    @Autowired
    private AlumnoLegajoRepository alumnoLegajoRepository;

    public List<AlumnoLegajoInscripcionCarreraDTO> obtenerAlumnosLegajos(String dato, String estado, String termino) {
        return alumnoLegajoRepository.obtenerAlumnosLegajos(dato, estado, termino);
    }

    public List<AlumnoLegajoInscripcionCarreraDTO> obtenerAlumnosMateriaCursadaId(Long dato, String estado) {
        return alumnoLegajoRepository.obtenerAlumnosMateriaCursadaId(dato, estado);
    }


    public List<AlumnoLegajoInscripcionCarreraDTO> obtenerAlumnosConCursadas(String dato, String estado, String termino) {
        return alumnoLegajoRepository.obtenerAlumnosConCursadas(dato, estado, termino);
    }


}
