package com.example.iesiback.services;

import com.example.iesiback.entities.Carrera;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public interface CarreraService {

    Carrera obtenerCarreraPorLegajoId(String legajoId);

    List<Carrera> findCarrerasByAlumnoDni(String alumnoDni);

    List<Carrera> obtenerCarreras();
    Carrera findCarreraById(String id);
    Optional<Carrera> obtenerCarreraPorId(String id);
    Carrera guardarCarrera(Carrera carrera);
    Carrera actualizarCarrera(String id, Carrera carrera);
    void eliminarCarrera(String id);
    List<Carrera> obtenerCarrerasOrdenadas();
    List<Carrera> obtenerCarreraInstcripcion(Long alumnoDni);
    String obtenerAnioCursada(String libretaEstudiantil) throws Exception;

    Integer obtenerDuracionCarrera(String libretaEstudiantil) throws Exception;
}
