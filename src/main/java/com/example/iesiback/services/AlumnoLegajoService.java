package com.example.iesiback.services;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.example.iesiback.dto.AlumnoLegajoInscripcionCarreraDTO;
import com.example.iesiback.entities.Legajo;
import com.example.iesiback.entities.Materia;
import com.example.iesiback.entities.MateriaCarrera;
import com.example.iesiback.enums.EstadoCondicion;
import com.example.iesiback.projection.AnioInicioProjection;
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

        List<AlumnoLegajoInscripcionCarreraDTO> alumnoLegajoInscripcionCarreraDTO =
                alumnoLegajoRepository.obtenerAlumnosLegajos(carreraId, estado, busqueda);

        if (alumnoLegajoInscripcionCarreraDTO.isEmpty()) {
            return alumnoLegajoInscripcionCarreraDTO;
        }

        List<String> legajoIds = alumnoLegajoInscripcionCarreraDTO.stream()
                .map(AlumnoLegajoInscripcionCarreraDTO::getLegajoId)
                .toList();

        // Validación batch: detecta legajos sin año de inicio (mismo chequeo que antes hacía obtenerAnioCursada)
        List<AnioInicioProjection> aniosInicio = alumnoLegajoRepository.findCursoBatch(legajoIds);
        Set<String> legajosConAnio = aniosInicio.stream()
                .map(AnioInicioProjection::getLegajoId)
                .collect(Collectors.toSet());

        List<String> legajosSinAnio = legajoIds.stream()
                .filter(id -> !legajosConAnio.contains(id))
                .toList();
        if (!legajosSinAnio.isEmpty()) {
            throw new IllegalArgumentException(
                    "No se encontró el año de inicio para las libretas: " + legajosSinAnio);
        }

        // Cálculo de curso batch (reemplaza el loop con obtenerAnioCursada por alumno)
        Map<String, String> cursoPorLegajo = materiaCarreraService.cursoPorMateriasActualBatch(legajoIds);
        for (AlumnoLegajoInscripcionCarreraDTO alumno : alumnoLegajoInscripcionCarreraDTO) {
            alumno.setCurso(cursoPorLegajo.getOrDefault(alumno.getLegajoId(), "Sin Datos"));
        }

        return alumnoLegajoInscripcionCarreraDTO;
    }




//        int diferencia = anioActual - anioInicio;
//        if (diferencia < 0) {
//            throw new IllegalStateException("El año de inicio es mayor al año actual.");
//        }
//
//        return switch (diferencia) {
//            case 0 -> "1er año";
//            case 1 -> "2do año";
//            case 2 -> "3er año";
//            default -> materiaCarreraService.cursoPorMateriasActual(libretaEstudiantil);
//        };
//    }



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


    public List<AlumnoLegajoInscripcionCarreraDTO> obtenerAlumnosConCursadas(
            String dato, String estado, String apellido, String comision) {

        String[] split = splitApellidoNombre(apellido);
        String apellidoFiltro = split[0];
        String nombreFiltro   = split[1];

        List<AlumnoLegajoInscripcionCarreraDTO> alumnoLegajoInscripcionCarreraDTO =
                alumnoLegajoRepository.obtenerAlumnosConCursadasNombre(dato, estado, apellidoFiltro, nombreFiltro, comision);

        if (alumnoLegajoInscripcionCarreraDTO.isEmpty()) {
            return alumnoLegajoInscripcionCarreraDTO;
        }

        List<String> legajoIds = alumnoLegajoInscripcionCarreraDTO.stream()
                .map(AlumnoLegajoInscripcionCarreraDTO::getLegajoId)
                .toList();

        // Validación batch: detecta legajos sin año de inicio (mismo chequeo que antes hacía obtenerAnioCursada)
        List<AnioInicioProjection> aniosInicio = alumnoLegajoRepository.findCursoBatch(legajoIds);
        Set<String> legajosConAnio = aniosInicio.stream()
                .map(AnioInicioProjection::getLegajoId)
                .collect(Collectors.toSet());

        List<String> legajosSinAnio = legajoIds.stream()
                .filter(id -> !legajosConAnio.contains(id))
                .toList();
        if (!legajosSinAnio.isEmpty()) {
            throw new IllegalArgumentException(
                    "No se encontró el año de inicio para las libretas: " + legajosSinAnio);
        }

        // Cálculo de curso batch (reemplaza el loop con obtenerAnioCursada por alumno)
        Map<String, String> cursoPorLegajo = materiaCarreraService.cursoPorMateriasActualBatch(legajoIds);
        for (AlumnoLegajoInscripcionCarreraDTO alumno : alumnoLegajoInscripcionCarreraDTO) {
            alumno.setCurso(cursoPorLegajo.getOrDefault(alumno.getLegajoId(), "Sin Datos"));
        }

        return alumnoLegajoInscripcionCarreraDTO;
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


    public List<AlumnoLegajoInscripcionCarreraDTO> obtenerAlumnosPorCarreraNombre(
            String carreraNombre, String estado, String apellido, String comision) {

        String[] split = splitApellidoNombre(apellido);
        String apellidoFiltro = split[0];
        String nombreFiltro   = split[1];

        List<AlumnoLegajoInscripcionCarreraDTO> alumnos =
                alumnoLegajoRepository.obtenerAlumnosConCursadasPorCarreraNombre(
                        carreraNombre, estado, apellidoFiltro, nombreFiltro, comision);

        if (alumnos.isEmpty()) {
            return alumnos;
        }

        List<String> legajoIds = alumnos.stream()
                .map(AlumnoLegajoInscripcionCarreraDTO::getLegajoId)
                .toList();

        // Validación batch: detecta legajos sin año de inicio (mismo chequeo que antes hacía obtenerAnioCursada)
        List<AnioInicioProjection> aniosInicio = alumnoLegajoRepository.findCursoBatch(legajoIds);
        Set<String> legajosConAnio = aniosInicio.stream()
                .map(AnioInicioProjection::getLegajoId)
                .collect(Collectors.toSet());

        List<String> legajosSinAnio = legajoIds.stream()
                .filter(id -> !legajosConAnio.contains(id))
                .toList();
        if (!legajosSinAnio.isEmpty()) {
            throw new IllegalArgumentException(
                    "No se encontró el año de inicio para las libretas: " + legajosSinAnio);
        }

        // Cálculo de curso batch (reemplaza el loop con obtenerAnioCursada por alumno)
        Map<String, String> cursoPorLegajo = materiaCarreraService.cursoPorMateriasActualBatch(legajoIds);
        for (AlumnoLegajoInscripcionCarreraDTO alumno : alumnos) {
            alumno.setCurso(cursoPorLegajo.getOrDefault(alumno.getLegajoId(), "Sin Datos"));
        }

        return alumnos;
    }
}
