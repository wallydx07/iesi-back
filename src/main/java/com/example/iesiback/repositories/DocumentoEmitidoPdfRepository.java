package com.example.iesiback.repositories;

import com.example.iesiback.entities.DocumentoEmitidoPdf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface DocumentoEmitidoPdfRepository extends JpaRepository<DocumentoEmitidoPdf, Long> {

    @Modifying
    @Query(value = """
        DELETE FROM documento_emitido_pdf p
        USING documento_emitido d
        WHERE p.documento_id = d.id
          AND d.fecha_vencimiento IS NOT NULL
          AND d.fecha_vencimiento < :limite
        """, nativeQuery = true)
    int borrarPdfsVencidosAntesDe(@Param("limite") LocalDate limite);
}