package com.example.iesiback.services;

import java.util.Arrays;
import java.util.List;
import com.example.iesiback.dto.AlumnoLegajoInscripcionCarreraDTO;
import com.example.iesiback.repositories.AlumnoLegajoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlumnoLegajoService {

    @Autowired
    private AlumnoLegajoRepository alumnoLegajoRepository;

    public List<AlumnoLegajoInscripcionCarreraDTO> obtenerAlumnosLegajos(String carreraId, String estado, String busqueda) {
        return alumnoLegajoRepository.obtenerAlumnosLegajos(carreraId, estado, busqueda);
    }


    public List<AlumnoLegajoInscripcionCarreraDTO> obtenerAlumnosMateriaCursadaId(Long dato, String estado) {
        return alumnoLegajoRepository.obtenerAlumnosMateriaCursadaId(dato, estado);
    }


//    public List<AlumnoLegajoInscripcionCarreraDTO> obtenerAlumnosConCursadas(String dato, String estado, String termino, String comision) {//incluyterecurssantes
////        return alumnoLegajoRepository.obtenerAlumnosConCursadas(dato, estado, termino, comision);
//        return alumnoLegajoRepository.obtenerAlumnosConOCinCursadas(dato, estado, termino, comision);
//    }

    public List<AlumnoLegajoInscripcionCarreraDTO> obtenerAlumnosConCursadas(
            String dato, String estado, String apellido, String comision) {
        String[] split = splitApellidoNombre(apellido);
        String apellidoFiltro = split[0];
        String nombreFiltro   = split[1];
        return alumnoLegajoRepository.obtenerAlumnosConCursadasNombre(dato, estado, apellidoFiltro, nombreFiltro, comision);
    }


    private String[] splitApellidoNombre(String input) {
        if (input == null || input.isBlank()) return new String[]{null, null};

        String[] partes = input.trim().split("\\s+");

        return switch (partes.length) {
            case 1 -> new String[]{partes[0], null};           // "Garcia"
            case 2 -> new String[]{partes[0], partes[1]};      // "Garcia Juan"
            default -> new String[]{                            // "Garcia Juan Carlos"
                    partes[0],
                    String.join(" ", Arrays.copyOfRange(partes, 1, partes.length))
            };
        };
    }


}
