package com.example.iesiback.repositories;

import com.example.iesiback.entities.MateriaCarrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MateriaCarreraRepository extends JpaRepository<MateriaCarrera, Long> {
    List<MateriaCarrera> findByCarrera_CarreraId(String carreraId);

}
