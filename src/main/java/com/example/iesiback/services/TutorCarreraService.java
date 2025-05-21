package com.example.iesiback.services;

import com.example.iesiback.entities.Carrera;

import java.util.List;

public interface TutorCarreraService {
    List<Carrera> obtenerCarrerasPorTutor(Long tutorDni);
  void asignarTutorACarrera(Integer tutorDni, String carreraId, String anio);
}