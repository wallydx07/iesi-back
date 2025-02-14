package com.example.iesiback.repositories;

import com.example.iesiback.entities.Cursada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursadaRepository extends JpaRepository<Cursada, Integer> {


    @Query("SELECT c FROM Cursada c WHERE c.cursadaLegajoId = :legajoId")
    List<Cursada> findByCursadaLegajoId(@Param("legajoId") String legajoId);
}
