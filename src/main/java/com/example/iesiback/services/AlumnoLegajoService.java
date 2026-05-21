package com.example.iesiback.services;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.example.iesiback.dto.AlumnoLegajoInscripcionCarreraDTO;
import com.example.iesiback.entities.Materia;
import com.example.iesiback.entities.MateriaCarrera;
import com.example.iesiback.enums.EstadoCondicion;
import com.example.iesiback.repositories.AlumnoLegajoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlumnoLegajoService {

    @Autowired
    private AlumnoLegajoRepository alumnoLegajoRepository;

    final public MateriaCarreraService materiaCarreraService;
    final public CorrelativaService correlativaService;

    public AlumnoLegajoService(MateriaCarreraService materiaCarreraService, CorrelativaService correlativaService) {
        this.materiaCarreraService = materiaCarreraService;
        this.correlativaService = correlativaService;
    }


    public List<AlumnoLegajoInscripcionCarreraDTO> obtenerAlumnosLegajos(String carreraId, String estado, String busqueda) {
        return alumnoLegajoRepository.obtenerAlumnosLegajos(carreraId, estado, busqueda);
    }

////obtiene alumnos desde asistencia
//    public List<AlumnoLegajoInscripcionCarreraDTO> obtenerAlumnosMateriaCursadaId(Long dato, String estado) {
//        return alumnoLegajoRepository.obtenerAlumnosMateriaCursadaId(dato, estado);
//    }
//


public List<AlumnoLegajoInscripcionCarreraDTO> obtenerAlumnosMateriaCursadaId(
        Long materiaCarreraId, String estado) {

    MateriaCarrera materiaCarrera = materiaCarreraService.findById(materiaCarreraId)
            .orElseThrow(() -> new RuntimeException("MateriaCarrera no encontrada"));

    Materia materia = materiaCarrera.getMateria();

    return alumnoLegajoRepository.obtenerAlumnosMateriaCursadaId(materiaCarreraId, estado)
            .stream()
            .filter(alumno -> {
                CorrelativaService.Veredicto v = correlativaService.evaluar(
                        alumno.getLegajoId(), materia.getMateriaOrden(), EstadoCondicion.CURSADA);
                return v.esAceptada();
            })
            .collect(Collectors.toList());
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
