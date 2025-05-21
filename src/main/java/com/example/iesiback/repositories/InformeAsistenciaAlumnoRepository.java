package com.example.iesiback.repositories;

import com.example.iesiback.entities.InformeAsistenciaAlumno;
import com.example.iesiback.entities.MateriaCarrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InformeAsistenciaAlumnoRepository extends JpaRepository<InformeAsistenciaAlumno, Integer> {

    // Buscar por materiaCarrera
    List<InformeAsistenciaAlumno> findByMateriaCarrera(MateriaCarrera materiaCarrera);

    // Buscar por fecha
    List<InformeAsistenciaAlumno> findByFecha(LocalDate fecha);

    // Buscar por materiaCarrera y fecha
    List<InformeAsistenciaAlumno> findByMateriaCarreraAndFecha(MateriaCarrera materiaCarrera, LocalDate fecha);

    List<InformeAsistenciaAlumno> findByMateriaCarrera_Id(Integer id);


}
