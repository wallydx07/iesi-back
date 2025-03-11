package com.example.iesiback.repositories;

import com.example.iesiback.dto.MateriaCarreraDTO;
import com.example.iesiback.entities.MateriaCarrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaCarreraRepository extends JpaRepository<MateriaCarrera, Long> {
    List<MateriaCarrera> findByCarrera_CarreraId(String carreraId);
    Optional<MateriaCarrera> findByCarrera_CarreraIdAndMateria_MateriaId(String carreraId, String materiaId);


/*
    @Query("SELECT new com.example.iesiback.dto.MateriaCarreraDTO(" +
            "m.fmcDocente, " +
            "m.libro, " +
            "m.folio," +
            "m.fecha," +
            "m.firma, " +
            "m.id," +
            "m.division," +
            " m.turno, " +
            "m.dia, " +
            "m.inicio, " +
            "m.fin," +
            "m.materia.materiaId," +
            "m.carrera.carreraId) " +
            "FROM MateriaCarrera m " +
            "WHERE m.materia.materiaId = :materiaId AND m.carrera.carreraId = :carreraId")
    MateriaCarreraDTO findMateriaCarreraByMateriaAndCarrera(String materiaId, String carreraId);
*/
    @Query(value = "SELECT COUNT(*) FROM materia_carrera mc INNER JOIN materia m ON mc.materia_id = m.materia_id WHERE mc.carrera_id = :carreraId AND m.materia_nivel = :nivel", nativeQuery = true)
    int countMateriasPorNivel(@Param("carreraId") String carreraId, @Param("nivel") String nivel);



    @Modifying
    @Transactional
    @Query("UPDATE MateriaCarrera m SET " +
            "m.libro = COALESCE(:libro, m.libro), " +
            "m.folio = COALESCE(:folio, m.folio), " +
            "m.fecha = COALESCE(:fecha, m.fecha), " +
            "m.firma = COALESCE(:firma, m.firma), " +
            "m.fmcDocente = COALESCE(:fmcDocente, m.fmcDocente), " +
            "m.division = COALESCE(:division, m.division), " +
            "m.turno = COALESCE(:turno, m.turno), " +
            "m.dia = COALESCE(:dia, m.dia), " +
            "m.inicio = COALESCE(:inicio, m.inicio), " +
            "m.fin = COALESCE(:fin, m.fin) " +
            "WHERE m.id = :id")
    int actualizarMateriaCarrera(@Param("id") Long id,
                                 @Param("libro") String libro,
                                 @Param("folio") String folio,
                                 @Param("fecha") LocalDate fecha,
                                 @Param("firma") boolean firma,
                                 @Param("fmcDocente") Integer fmcDocente,
                                 @Param("division") String division,
                                 @Param("turno") String turno,
                                 @Param("dia") String dia,
                                 @Param("inicio") String inicio,
                                 @Param("fin") String fin);


}

