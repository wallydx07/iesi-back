package com.example.iesiback.repositories;

import com.example.iesiback.dto.AporteDTO;
import com.example.iesiback.entities.Aporte;
import com.example.iesiback.entities.Legajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AporteRepository extends JpaRepository<Aporte, Integer> {
    // Puedes agregar métodos de consulta personalizados aquí si lo requieres
    @Query("select new com.example.iesiback.dto.AporteDTO(" +
            "a.id, " +
            "al.alumnoDni, " +
            "al.alumnoApellido, " +
            "al.alumnoNombre, " +
            "l.legajoId, " +
            "a.aporteMonto, " +
            "a.aporteNroRecibo, " +
            "a.aporteTalonarioRecibo, " +
            "a.aporteFecha, " +
            "a.aporteObs, " +
            "a.usuario, " +
            "a.validado) " +
            "from Aporte a " +
            "join a.aporteLegajo l " +
            "join l.legajoAlumnoDni al " +
            "where a.aporteMonto <> 0 " +
            "order by a.id desc")
    List<AporteDTO> findAportesConDatos();

    List<Aporte> findByAporteLegajo(Legajo aporteLegajo);

//    @Query("SELECT a FROM Aporte a WHERE a.aporteLegajo.legajoId = :legajoId AND YEAR(a.aporteFecha) = YEAR(CURRENT_DATE)")
//    List<Aporte> findAportesDelAnioActualPorLegajo(@Param("legajoId") String legajoId);

    @Query(value = "SELECT * FROM aporte a " +
            "WHERE a.aporte_legajo_id = :legajoId " +
            "AND a.aporte_fecha >= DATE_TRUNC('year', CURRENT_DATE) - INTERVAL '1 month' " +
            "AND a.aporte_monto > 0",
            nativeQuery = true)
    List<Aporte> findAportesDelAnioActualPorLegajo(@Param("legajoId") String legajoId);



}
