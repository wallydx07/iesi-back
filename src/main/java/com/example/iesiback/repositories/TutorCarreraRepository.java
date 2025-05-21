package com.example.iesiback.repositories;

import com.example.iesiback.entities.TutorCarrera;
import com.example.iesiback.entities.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TutorCarreraRepository extends JpaRepository<TutorCarrera, Long> {

    @Query("SELECT tc.carrera FROM TutorCarrera tc WHERE tc.tutor.id = :dni")
    List<Carrera> findCarrerasPorTutor(@Param("dni") Long dni);


}