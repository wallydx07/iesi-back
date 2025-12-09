package com.example.iesiback.repositories;

import com.example.iesiback.entities.Persona;
import com.example.iesiback.entities.Legajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface LegajoRepository extends JpaRepository<Legajo, String> {

    @Query(value = "SELECT legajo_id FROM legajo WHERE legajo_id LIKE CONCAT(:prefijo, '%') ORDER BY CAST(SUBSTRING(legajo_id FROM LENGTH(:prefijo) + 1) AS INTEGER) DESC LIMIT 1", nativeQuery = true)
    String findMaxLegajoId(@Param("prefijo") String prefijo);

//    @Query(value = """
//    SELECT legajo_id
//    FROM legajo
//    WHERE legajo_id LIKE :prefijo || '%'
//    AND SUBSTRING(legajo_id FROM LENGTH(:prefijo) + 1) ~ '^[0-9]+$'
//    ORDER BY CAST(SUBSTRING(legajo_id FROM LENGTH(:prefijo) + 1) AS INTEGER) DESC
//    LIMIT 1
//""", nativeQuery = true)
//    String findMaxLegajoId(@Param("prefijo") String prefijo);

    @Modifying
    @Transactional
    @Query("UPDATE Legajo l SET l.legajoPersonaDni = :nuevoDni WHERE l.legajoPersonaDni = :dniActual")
    int actualizarAlumnoDni(@Param("dniActual") Long dniActual, @Param("nuevoDni") Long nuevoDni);

    List<Legajo> findByLegajoPersonaDni(Persona persona);

}
