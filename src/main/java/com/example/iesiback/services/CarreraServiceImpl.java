package com.example.iesiback.services;
import com.example.iesiback.entities.Carrera;
import com.example.iesiback.repositories.CarreraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CarreraServiceImpl implements CarreraService {

    private final InscripcionService inscripcionService;

    @Autowired
    public CarreraServiceImpl(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @Autowired
    private CarreraRepository carreraRepository;

    @Override
    public List<Carrera> findCarrerasByAlumnoDni(String alumnoDni) {
        return carreraRepository.findCarreraIdByAlumnoDni(alumnoDni);
    }

    @Override
    public List<Carrera> obtenerCarreras() {
        return carreraRepository.findAll();
    }


    @Override
    public Carrera findCarreraById(String id) {
        return carreraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrera no encontrado con ID: " + id));
    }

    @Override
    public Optional<Carrera> obtenerCarreraPorId(String id) {
        return carreraRepository.findById(id);
    }

    @Override
    public Carrera guardarCarrera(Carrera carrera) {
        return carreraRepository.save(carrera);
    }

    @Override
    public Carrera actualizarCarrera(String id, Carrera carrera) {
        Optional<Carrera> carreraExistente = carreraRepository.findById(id);
        if (carreraExistente.isPresent()) {
            carrera.setCarreraId(id); // Mantener el mismo ID
            return carreraRepository.save(carrera);
        }
        return null;
    }

    @Override
    public void eliminarCarrera(String id) {
        carreraRepository.deleteById(id);
    }

    @Override
    public List<Carrera> obtenerCarrerasOrdenadas() {
        return carreraRepository.findAllOrderedByYearAndName();
    }

    @Override
    public List<Carrera> obtenerCarreraInstcripcion(Long alumnoDni) {
        List<Carrera> carreras = this.obtenerCarreras();
        carreras.removeIf(carrera -> inscripcionService.existsByAlumnoDniAndCarreraNombre(alumnoDni, carrera.getCarreraNombre()));
        return carreras;
    }
}
