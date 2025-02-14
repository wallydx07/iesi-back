package com.example.iesiback.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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

}
