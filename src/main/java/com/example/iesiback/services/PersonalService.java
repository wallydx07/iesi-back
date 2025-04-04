package com.example.iesiback.services;

import com.example.iesiback.entities.Personal;

import java.util.List;
import java.util.Optional;

public interface PersonalService {

    List<Personal> findAll();

    Optional<Personal> findById(String id);

    Personal save(Personal personal);

    void deleteById(String id);

    boolean existsByDni(String dni);
}