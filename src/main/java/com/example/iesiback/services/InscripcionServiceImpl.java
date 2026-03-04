package com.example.iesiback.services;
import com.example.iesiback.entities.Carrera;
import com.example.iesiback.entities.Inscripcion;
import com.example.iesiback.repositories.InscripcionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class InscripcionServiceImpl implements InscripcionService {

    private final InscripcionRepository inscripcionRepository;

    public InscripcionServiceImpl(InscripcionRepository inscripcionRepository) {
        this.inscripcionRepository = inscripcionRepository;
    }
            @Override
    public Inscripcion crearInscripcion(Inscripcion inscripcion) {
        return inscripcionRepository.save(inscripcion);
    }

    @Override
    public Inscripcion findByLegajoId(String legajoId) {
        return inscripcionRepository.findByLegajo_LegajoId(legajoId);
    }


    @Override
    public boolean existsByAlumnoDniAndCarreraNombre(String alumnoDni, String carreraNombre) {
        return inscripcionRepository.existsByAlumnoDniAndCarreraNombre(alumnoDni,carreraNombre);
    }

//    @Override
//    public Inscripcion existsByAlumnoDniAndCarreraId(String alumnoDni, String carreraNombre) {
//        return inscripcionRepository.f
//                existsByAlumnoDniAndCarreraNombre(alumnoDni,carreraNombre);
//    }

@Override
public List<String> rellenarAnyo(String legajoId) {
        List<String> opciones = new ArrayList<>();
        // Obtener la inscripción del estudiante
        Inscripcion inscripcionOpt = inscripcionRepository.findInscripcionByLegajoId(legajoId);
        int fin = Calendar.getInstance().get(Calendar.YEAR);
        int ini = inscripcionOpt.getCarrera().getCarreraYear();
        System.out.println("ini: " + ini + " fin: " + fin);

        int anio = fin - ini;
        System.out.println("anio: " + anio);

        // Determinar el estado del estudiante y agregar las opciones
        switch (anio) {
            case 0:
                opciones.add("es estudiante del 1er año de la: ");
                break;
            case 1:
                opciones.add("es estudiante del 2do año de la: ");
                break;
            case 2:
                opciones.add("es estudiante del 3er año de la: ");
                break;
            default:
                opciones.add("es estudiante de la carrera de: ");
                break;
        }
            opciones.add("ha egresado de la carrera: ");
            opciones.add("se encuentra cursando la carrera: ");
            opciones.add("curso la carrera: ");
        return opciones;
    }



}