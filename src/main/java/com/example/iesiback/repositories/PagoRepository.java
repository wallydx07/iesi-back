package com.example.iesiback.repositories;


import com.example.iesiback.dto.AlumnoLegajoInscripcionCarreraDTO;
import com.example.iesiback.entities.Tramite;
import com.example.iesiback.entities.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    // Buscar por ID del pago de Mercado Pago
    Optional<Pago> findByMpPaymentId(Long mpPaymentId);

    // Buscar por preference ID (referencia de la preferencia de pago)
    Optional<Pago> findByPreferenceId(String preferenceId);

    // Buscar por external reference (puede ser atencion-123, etc.)
    Optional<Pago> findByExternalReference(String externalReference);

    // Buscar todos los pagos de una atención
    List<Pago> findByTramite(Tramite atencion);

    // Buscar por estado (ej: approved, rejected)
    List<Pago> findByEstado(String estado);

    // Buscar pagos por método de pago (ej: visa, mastercard)
    List<Pago> findByMetodoPago(String metodoPago);

    // Buscar pagos por tipo de pago (ej: credit_card, ticket)
    List<Pago> findByTipoPago(String tipoPago);

    // Buscar el único pago relacionado a una atención por su ID
    Optional<Pago> findByTramiteId(Integer atencionId);



    @Query("SELECT new com.example.iesiback.dto.ResumenRecaudacionDTO(" +
            "l.legajoId, " +
            "a.personaDni, " +
            "a.personaApellido, " +
            "a.personaNombre, " +
            "c.carreraId, " +
            "l.legajoFotocopiaDni, " +
            "l.legajoCertificadoNacimiento, " +
            "l.legajoFotocopiaTitulo, " +
            "l.legajoPlanillaProntuarial, " +
            "l.legajoCarnetSanitario, " +
            "l.legajoFoto, " +
            "l.legajoAval, " +
            "l.legajoCarpetaColgante, " +
            "l.usuario," +
            "a.personaFechaNacimiento," +
            "a.personaCorreo," +
            "a.personaDomicilioCelular," +
            "l.legajoComision " +
            ") " +
            "FROM Legajo l " +
            "JOIN l.legajoPersonaDni a " +
            "JOIN Inscripcion i ON i.legajo = l " +
            "JOIN i.carrera c " +
            "WHERE (:carreraId IS NULL OR c.carreraId LIKE :carreraId) " +
            "AND (:estado IS NULL OR l.legajoEstado = :estado) " +
            "AND (:busqueda IS NULL OR a.personaApellido LIKE :busqueda OR a.personaNombre LIKE :busqueda OR CONCAT(a.personaApellido, ' ', a.personaNombre) LIKE :busqueda OR CAST(a.personaDni AS string) LIKE :busqueda) " +
            "ORDER BY a.personaApellido ASC, a.personaNombre ASC")
    List<AlumnoLegajoInscripcionCarreraDTO> obtenerAlumnosLegajos(
            @Param("carreraId") String carreraId,
            @Param("estado") String estado,
            @Param("busqueda") String busqueda);

}
