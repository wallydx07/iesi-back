package com.example.iesiback.services;
import com.example.iesiback.entities.Inscripcion;
import com.example.iesiback.repositories.InscripcionRepository;
import org.springframework.stereotype.Service;

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
    public boolean existsByAlumnoDniAndCarreraNombre(Long alumnoDni, String carreraId) {
        return inscripcionRepository.existsByAlumnoDniAndCarreraNombre(alumnoDni,carreraId);
    }

}