package com.example.iesiback.repositories;

import com.example.iesiback.entities.Tramite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TramiteRepository extends JpaRepository<Tramite, Integer> {
    List<Tramite> findByTramiteDni(Long dni);
    List<Tramite> findByTramiteApellidoNombreContainingIgnoreCase(String apellidoNombre);
    List<Tramite> findByTramiteCorreoContainingIgnoreCase(String correo);
    List<Tramite> findByTramiteDestino(String destino);
    List<Tramite> findByTramiteUsuario(String usuario);
    List<Tramite> findByTramiteEstado(String resuelto);
    List<Tramite> findByTramiteFecha(LocalDate fecha);
    Optional<Tramite> findByCodigoSeguimiento(String codigoSeguimiento);
    List<Tramite> findAllByOrderByIdDesc();
    List<Tramite> findByGestorDni(Long gestorDni);
    List<Tramite> findByTramiteReferencia(Integer atencionReferencia);
    List<Tramite> findAllByOrderByTramiteFechaDescNumeroTipoDesc();
    List<Tramite> findByLegajoId(String atencionLegajo);


    @Query(value = """
    WITH RECURSIVE ascendentes AS (
        SELECT t.tramite_id, t.tramite_estado, t.tramite_dni, t.tramite_apellido_nombre,
               t.tramite_correo, t.tramite_celular, t.tramite_tipo, t.tramite_problema,
               t.tramite_fecha, t.tramite_respuesta, t.tramite_observaciones,
               t.tramite_destino, t.tramite_usuario, t.codigo_seguimiento,
               t.numero_tipo, t.tramite_prioridad, t.tramite_folios, t.legajo_id,
               t.gestor_dni, t.tramite_canal, t.tramite_subtipo, t.tramite_asunto,
               t.tramite_referencia,
               ARRAY[t.tramite_id] AS visitados
        FROM tramite t
        WHERE t.tramite_id = :id

        UNION ALL

        SELECT t.tramite_id, t.tramite_estado, t.tramite_dni, t.tramite_apellido_nombre,
               t.tramite_correo, t.tramite_celular, t.tramite_tipo, t.tramite_problema,
               t.tramite_fecha, t.tramite_respuesta, t.tramite_observaciones,
               t.tramite_destino, t.tramite_usuario, t.codigo_seguimiento,
               t.numero_tipo, t.tramite_prioridad, t.tramite_folios, t.legajo_id,
               t.gestor_dni, t.tramite_canal, t.tramite_subtipo, t.tramite_asunto,
               t.tramite_referencia,
               a.visitados || t.tramite_id
        FROM tramite t
        INNER JOIN ascendentes a ON t.tramite_id = a.tramite_referencia
        WHERE NOT t.tramite_id = ANY(a.visitados)
    ),
    descendentes AS (
        SELECT t.tramite_id, t.tramite_estado, t.tramite_dni, t.tramite_apellido_nombre,
               t.tramite_correo, t.tramite_celular, t.tramite_tipo, t.tramite_problema,
               t.tramite_fecha, t.tramite_respuesta, t.tramite_observaciones,
               t.tramite_destino, t.tramite_usuario, t.codigo_seguimiento,
               t.numero_tipo, t.tramite_prioridad, t.tramite_folios, t.legajo_id,
               t.gestor_dni, t.tramite_canal, t.tramite_subtipo, t.tramite_asunto,
               t.tramite_referencia,
               ARRAY[t.tramite_id] AS visitados
        FROM tramite t
        WHERE t.tramite_id = :id

        UNION ALL

        SELECT t.tramite_id, t.tramite_estado, t.tramite_dni, t.tramite_apellido_nombre,
               t.tramite_correo, t.tramite_celular, t.tramite_tipo, t.tramite_problema,
               t.tramite_fecha, t.tramite_respuesta, t.tramite_observaciones,
               t.tramite_destino, t.tramite_usuario, t.codigo_seguimiento,
               t.numero_tipo, t.tramite_prioridad, t.tramite_folios, t.legajo_id,
               t.gestor_dni, t.tramite_canal, t.tramite_subtipo, t.tramite_asunto,
               t.tramite_referencia,
               d.visitados || t.tramite_id
        FROM tramite t
        INNER JOIN descendentes d ON t.tramite_referencia = d.tramite_id
        WHERE NOT t.tramite_id = ANY(d.visitados)
    )
    SELECT tramite_id, tramite_estado, tramite_dni, tramite_apellido_nombre,
           tramite_correo, tramite_celular, tramite_tipo, tramite_problema,
           tramite_fecha, tramite_respuesta, tramite_observaciones,
           tramite_destino, tramite_usuario, codigo_seguimiento,
           numero_tipo, tramite_prioridad, tramite_folios, legajo_id,
           gestor_dni, tramite_canal, tramite_subtipo, tramite_asunto,
           tramite_referencia
    FROM ascendentes
    UNION
    SELECT tramite_id, tramite_estado, tramite_dni, tramite_apellido_nombre,
           tramite_correo, tramite_celular, tramite_tipo, tramite_problema,
           tramite_fecha, tramite_respuesta, tramite_observaciones,
           tramite_destino, tramite_usuario, codigo_seguimiento,
           numero_tipo, tramite_prioridad, tramite_folios, legajo_id,
           gestor_dni, tramite_canal, tramite_subtipo, tramite_asunto,
           tramite_referencia
    FROM descendentes
    """, nativeQuery = true)
    List<Tramite> findArbolCompleto(@Param("id") Long id);

}