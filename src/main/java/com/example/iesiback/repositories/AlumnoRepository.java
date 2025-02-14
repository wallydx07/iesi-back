package com.example.iesiback.repositories;
import com.example.iesiback.entities.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlumnoRepository extends JpaRepository<Alumno, String> {
}
