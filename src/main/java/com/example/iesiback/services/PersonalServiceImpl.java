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

}