package com.example.iesiback.repositories;

import com.example.iesiback.entities.Cursada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CursadaRepository extends JpaRepository<Cursada, Integer> {

    @Query("SELECT c FROM Cursada c " +
            "JOIN c.cursadaMateriaCarrera mc " +
            "JOIN mc.materia m " +
            "WHERE m.materiaOrden = :materiaOrden AND c.legajo.legajoId = :legajoId")
    Optional<Cursada> findByMateriaOrdenAndLegajoId(@Param("materiaOrden") String materiaOrden,
                                                    @Param("legajoId") String legajoId);

    @Query("SELECT c FROM Cursada c LEFT JOIN FETCH c.notas WHERE c.legajo.legajoId = :legajoId")
    List<Cursada> findByLegajoId(@Param("legajoId") String legajoId);
}



