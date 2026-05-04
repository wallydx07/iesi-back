package com.example.iesiback.repositories;


import com.example.iesiback.dto.AlumnoLegajoInscripcionCarreraDTO;
import com.example.iesiback.entities.Tramite;
import com.example.iesiback.entities.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
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


    List<Pago> findByFechaPago(Instant fechaPago);




    List<Pago> findByFechaPagoBetween(Instant inicio, Instant fin);



}
