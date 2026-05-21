package com.example.iesiback.services;

import com.example.iesiback.dto.*;
import com.example.iesiback.entities.AsistenciaPersonal;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AsistenciaPersonalService {

    // ==========================================
    //          CRUD BÁSICO
    // ==========================================
    List<AsistenciaPersonal> obtenerTodas();
    List<AsistenciaPersonal> buscarPorDni(Long dni);
    Optional<AsistenciaPersonal> buscarPorId(Integer id);
    AsistenciaPersonal guardar(AsistenciaPersonal asistencia);
    void eliminar(Integer id);
    void actualizarAsistencia(Integer id, AsistenciaPersonal update);

    // ==========================================
    //     PROCESAMIENTO DEL BIOMÉTRICO (E/S)
    // ==========================================
    RespuestaAsistenciaDTO AsistenciaDahua(RegistroAsistenciaDTO dto);
    List<AsistenciaPersonal> obtenerPorDniYFecha(Long dni, LocalDate fecha);

    // ==========================================
    //       CONSULTAS, REPORTES Y DETALLES
    // ==========================================
    List<DetalleAsistenciaPersonalDTO> obtenerDetallePorFecha(LocalDate fecha);
    List<DetalleAsistenciaPersonalDTO> obtenerDetallePorYear(Integer year);
    List<DetalleAsistenciaPersonalDTO> obtenerDetallePorDNI(Long dni);
    List<AsistenciaDetalleDTO> obtenerAsistenciasPorFecha(LocalDate fecha);
    List<ReporteFaltasDTO> cargarAsistenciasDelMesRango(LocalDate fechaInicio, LocalDate fechaFin);

    // ==========================================
    //          ACTUALIZACIONES MASIVAS
    // ==========================================
    @Transactional
    int actualizarObservacionesYEstadoPorFecha(String observaciones, String estado, LocalDate fecha);

    boolean existeAsistenciaParaHoy(Long horarioId, LocalDate fecha);
}