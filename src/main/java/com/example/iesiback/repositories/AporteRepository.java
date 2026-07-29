package com.example.iesiback.repositories;

import com.example.iesiback.dto.AporteDTO;
import com.example.iesiback.entities.Aporte;
import com.example.iesiback.entities.Tramite;
import com.example.iesiback.entities.Pago;
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
            "al.personaDni, " +
            "al.personaApellido, " +
            "al.personaNombre, " +
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
            "join l.legajoPersonaDni al " +
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

    @Query(value = """
    SELECT
        aporte_id,
        aporte_legajo_id,
        aporte_nro_recibo,
        aporte_talonario_recibo,
        aporte_monto,
        aporte_fecha,
        aporte_obs,
        validado,
        usuario
    FROM aporte
    WHERE aporte_legajo_id = :legajoId

    UNION ALL

    SELECT
        (tramite.tramite_id) AS aporte_id,
        tramite.legajo_id              AS aporte_legajo_id,
        tramite.tramite_id             AS aporte_nro_recibo,
        tramite.tramite_id             AS aporte_talonario_recibo,
        pago.monto_total                AS aporte_monto,
        pago.fecha_pago                 AS aporte_fecha,
        'TRAMITE'                        AS aporte_obs,
        CASE WHEN pago.estado = 'APROBADO' THEN true ELSE false END AS validado,
        pago.responsable                AS usuario
    FROM pago
    INNER JOIN tramite ON pago.tramite_id = tramite.tramite_id
    WHERE tramite.legajo_id = :legajoId and tramite.tramite_tipo= 'Matricula'

    ORDER BY aporte_fecha DESC
    """, nativeQuery = true)
    List<Aporte> buscarUnificadoPorLegajo(@Param("legajoId") String legajoId);

}
