package com.example.iesiback.repositories;

import com.example.iesiback.entities.MateriaCarrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaCarreraRepository extends JpaRepository<MateriaCarrera, Long> {
    List<MateriaCarrera> findByCarrera_CarreraId(String carreraId);
    Optional<MateriaCarrera> findByCarrera_CarreraIdAndMateria_MateriaId(String carreraId, String materiaId);

    @Query(value = "SELECT COUNT(*) FROM materia_carrera mc INNER JOIN materia m ON mc.materia_id = m.materia_id WHERE mc.carrera_id = :carreraId AND m.materia_nivel = :nivel", nativeQuery = true)
    int countMateriasPorNivel(@Param("carreraId") String carreraId, @Param("nivel") String nivel);

}
