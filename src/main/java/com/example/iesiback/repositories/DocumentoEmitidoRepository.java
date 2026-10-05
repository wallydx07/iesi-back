package com.example.iesiback.repositories;

import com.example.iesiback.entities.DocumentoEmitido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DocumentoEmitidoRepository extends JpaRepository<DocumentoEmitido, Long> {

    // Para la verificación pública: trae documento + certificado + trámite en una sola consulta
    @Query("""
        SELECT d FROM DocumentoEmitido d
        JOIN FETCH d.certificado c
        JOIN FETCH c.tramite
        WHERE d.token = :token
        """)
    Optional<DocumentoEmitido> findByTokenConTramite(@Param("token") UUID token);

    // Para obtenerOEmitir: el documento activo de un certificado
    Optional<DocumentoEmitido> findByCertificadoIdAndAnuladaFalse(Integer certificadoId);
}