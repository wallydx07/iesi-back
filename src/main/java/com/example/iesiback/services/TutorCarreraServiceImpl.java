package com.example.iesiback.services;

import com.example.iesiback.entities.Carrera;
import com.example.iesiback.entities.TutorCarrera;
import com.example.iesiback.repositories.CarreraRepository;
import com.example.iesiback.repositories.TutorCarreraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TutorCarreraServiceImpl implements TutorCarreraService {

    @Autowired
    private TutorCarreraRepository tutorCarreraRepository;

    @Autowired
    private CarreraRepository carreraRepository;

    @Override
    public List<Carrera> obtenerCarrerasPorTutor(Long tutorDni) {
        return tutorCarreraRepository.findCarrerasPorTutor(tutorDni);
    }


    @Override
    public void asignarTutorACarrera(Integer tutorDni, String carreraId, String anio) {
        TutorCarrera tc = new TutorCarrera();
        tc.setId(tutorDni);
        tc.setCarrera(carreraRepository.findById(carreraId).orElseThrow());
        tc.setAnio(anio);
        tutorCarreraRepository.save(tc);
    }
}
