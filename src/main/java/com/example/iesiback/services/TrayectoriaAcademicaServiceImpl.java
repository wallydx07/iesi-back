package com.example.iesiback.services;

import com.example.iesiback.dto.NotaImportDTO;
import com.example.iesiback.dto.ResultadoImportDTO;
import com.example.iesiback.enums.EstadoNota;
import com.example.iesiback.enums.NotaCondicion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrayectoriaAcademicaServiceImpl implements TrayectoriaAcademicaService {

    private final NotaFilaProcessor notaFilaProcessor;
    private final MateriaCarreraService materiaCarreraService;

    @Override
    public ResultadoImportDTO procesarNotas(List<NotaImportDTO> filas) {
        log.info("========= INICIO PROCESAMIENTO ==========");
        log.info("Cantidad de filas recibidas: {}", filas.size());
        int filaNumero = 0;
        int exitosas = 0;
        List<String> errores = new ArrayList<>();
        for (NotaImportDTO fila : filas) {
            filaNumero++;
            log.info("--------------------------------------------------");
            log.info("Fila #{}", filaNumero);
            log.info("Datos crudos -> DNI: {} | Nombre: {} | Apellido: {} | CarreraId: {} | MateriaOrden: {} | Fecha: {} | Condición: {}",
                    fila.getDni(),
                    fila.getNombre(),
                    fila.getApellido(),
                    fila.getCarreraId(),
                    fila.getMateriaOrden(),
                    fila.getFecha(),
                    fila.getCondicion());
            // ── Validaciones previas (no requieren transacción) ──────────────
            fila.setNotaEstado(EstadoNota.APROBADO);
            if (fila.getDni() == null) {
                String msg = "Fila #" + filaNumero + " ignorada -> DNI nulo";
                log.warn("⚠ {}", msg);
                errores.add(msg);
                continue;
            }
            if (fila.getCarreraId() == null) {
                String msg = "Fila #" + filaNumero + " ignorada -> CarreraId nulo";
                log.warn("⚠ {}", msg);
                errores.add(msg);
                continue;
            }
            if (fila.getMateriaOrden() == null) {
                String msg = "Fila #" + filaNumero + " ignorada -> MateriaOrden nulo";
                log.warn("⚠ {}", msg);
                errores.add(msg);
                continue;
            }
            if (fila.getCondicion() == null) {
                fila.setCondicion(NotaCondicion.CURSADA);//promocion==========================================
                fila.setNotaEstado(EstadoNota.CURSANDO);
            }
            if (fila.getFecha() == null) {
                LocalDate aux = materiaCarreraService.obtenerFechaVigencia(
                        fila.getCarreraId(), fila.getMateriaOrden());
                if (aux == null) {
                    String msg = "Fila #" + filaNumero + " ignorada -> Fecha nula y sin fecha de vigencia disponible";
                    log.warn("⚠ {}", msg);
                    errores.add(msg);
                    continue;
                }
                log.warn("⚠ Fecha nula en fila #{}, se completó con fecha de vigencia: {}", filaNumero, aux);
                fila.setFecha(aux);
            }
            // ── Procesamiento atómico por fila ───────────────────────────────
            // Cada llamada a notaFilaProcessor.procesarFila() corre en su propia
            // transacción (REQUIRES_NEW). Si falla, solo hace rollback de esa fila.
            try {
                log.info("Procesando DNI: {} | MateriaOrden: {} | Condición: {}",
                        fila.getDni(), fila.getMateriaOrden(), fila.getCondicion());
                notaFilaProcessor.procesarFila(fila);
                exitosas++;
                log.info("✅ Fila #{} procesada correctamente.", filaNumero);

            } catch (Exception e) {
                String msg = "Fila #" + filaNumero
                        + " | DNI: " + fila.getDni()
                        + " | Error: " + e.getMessage();
                log.error("💥 {}", msg, e);
                errores.add(msg);
                // No relanzamos la excepción -> el loop continúa con la siguiente fila
            }
        }

        log.info("========== FIN PROCESAMIENTO ==========");
        log.info("Total filas: {} | Exitosas: {} | Con error: {}",
                filas.size(), exitosas, errores.size());

        return new ResultadoImportDTO(exitosas, errores);
    }
}