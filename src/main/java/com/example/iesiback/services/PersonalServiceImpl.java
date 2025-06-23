package com.example.iesiback.services;

import com.example.iesiback.entities.Personal;
import com.example.iesiback.repositories.PersonalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonalServiceImpl implements PersonalService {

    private final PersonalRepository personalRepository;

    @Autowired
    public PersonalServiceImpl(PersonalRepository personalRepository) {
        this.personalRepository = personalRepository;
    }

    @Override
    public List<Personal> findAll() {
        return personalRepository.findAll();
    }

    @Override
    public Optional<Personal> findById(String id) {
        return personalRepository.findById(id);
    }

    @Override
    public Personal save(Personal personal) {
        return personalRepository.save(personal);
    }

    @Override
    public void deleteById(String id) {
        personalRepository.deleteById(id);
    }

    @Override
    public boolean existsByDni(String dni) {
        return personalRepository.existsById(dni);
    }


    @Override
    public List<Personal> findByPersonalNombre(String personalNombre) {
        return personalRepository.findByPersonalNombreContainingIgnoreCase(personalNombre);
    }

    @Override
    public List<Personal> findByPersonalApellido(String personalApellido) {
        return personalRepository.findByPersonalApellidoContainingIgnoreCase(personalApellido);
    }

    @Override
    public List<Personal> findByPersonalCorreo(String personalCorreo) {
        return personalRepository.findByPersonalCorreoContainingIgnoreCase(personalCorreo);
    }

    @Override
    public List<Personal> findByPersonalCelular(String personalCelular) {
        return personalRepository.findByPersonalCelularContainingIgnoreCase(personalCelular);
    }

    @Override
    public List<Personal> findByPersonalTipo(String personalTipo) {
        return personalRepository.findByPersonalTipoContainingIgnoreCase(personalTipo);
    }

    @Override
    public List<Personal> findByPersonalRdni(Boolean personalRdni) {
        return personalRepository.findByPersonalRdni(personalRdni);
    }

    @Override
    public List<Personal> findByPersonalRresidencia(Boolean personalRresidencia) {
        return personalRepository.findByPersonalRresidencia(personalRresidencia);
    }

    @Override
    public List<Personal> findByPersonalRplanilla(Boolean personalRplanilla) {
        return personalRepository.findByPersonalRplanilla(personalRplanilla);
    }

    @Override
    public List<Personal> findByPersonalRsanitario(Boolean personalRsanitario) {
        return personalRepository.findByPersonalRsanitario(personalRsanitario);
    }

    @Override
    public List<Personal> findByPersonalRnacimiento(Boolean personalRnacimiento) {
        return personalRepository.findByPersonalRnacimiento(personalRnacimiento);
    }

    @Override
    public List<Personal> findByDeclaracion(Boolean declaracion) {
        return personalRepository.findByDeclaracion(declaracion);
    }

    @Override
    public List<Personal> findByCargo(Boolean cargo) {
        return personalRepository.findByCargo(cargo);
    }

    @Override
    public List<Personal> findByCuil(Boolean cuil) {
        return personalRepository.findByCuil(cuil);
    }

    @Override
    public List<Personal> findByTitulo(Boolean titulo) {
        return personalRepository.findByTitulo(titulo);
    }

    @Override
    public List<Personal> findByCurriculum(Boolean curriculum) {
        return personalRepository.findByCurriculum(curriculum);
    }

    @Override
    public List<Personal> buscarPorTextoLibre(String texto) {
        return personalRepository.buscarPorTextoLibre(texto);
    }


}