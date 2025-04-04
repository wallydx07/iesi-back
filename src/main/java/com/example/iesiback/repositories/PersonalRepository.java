package com.example.iesiback.repositories;

import com.example.iesiback.entities.Personal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalRepository extends JpaRepository<Personal, String> {
    boolean existsById(String id);
}
