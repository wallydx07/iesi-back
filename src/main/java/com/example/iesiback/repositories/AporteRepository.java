package com.example.iesiback.repositories;

import com.example.iesiback.dto.AporteDTO;
import com.example.iesiback.entities.Aporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
            "a.usuario) " +
            "from Aporte a " +
            "join a.aporteLegajo l " +
            "join l.legajoAlumnoDni al " +
            "where a.aporteMonto <> 0 " +
            "order by a.aporteFecha desc")
    List<AporteDTO> findAportesConDatos();
}
