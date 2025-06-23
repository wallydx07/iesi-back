package com.example.iesiback.services;

import com.example.iesiback.dto.*;
import com.example.iesiback.entities.AsistenciaPersonal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AsistenciaPersonalService {
    List<AsistenciaPersonal> obtenerTodas();
    List<AsistenciaPersonal> buscarPorDni(Long dni);
    Optional<AsistenciaPersonal> buscarPorId(Integer id);
    AsistenciaPersonal guardar(AsistenciaPersonal asistencia);
    void eliminar(Integer id);
    boolean verificarSiYaEstaRegistrado(LocalDate fecha, Long dni, Integer horarioId);
    void registrarEntrada(LocalDate fecha, LocalTime ahora, Long dni, HorarioDTO horario);
    RespuestaAsistenciaDTO AsistenciaDahua(RegistroAsistenciaDTO dto);
    boolean marcarHoraSalida(LocalDate fecha, LocalTime ahora, Long dni, HorarioDTO horario);
    List<DetalleAsistenciaPersonalDTO> obtenerDetallePorFecha(LocalDate fecha);
    boolean existeAsistenciaParaHoy(Long horarioId, LocalDate fecha);
    List<DetalleAsistenciaPersonalDTO> obtenerDetallePorYear(Integer year);
    void actualizarAsistencia(Integer id, AsistenciaPersonal update);
    List<DetalleAsistenciaPersonalDTO> obtenerDetallePorDNI(Long dni);
    List<ReporteFaltasDTO> cargarAsistenciasDelMes(int mes);
    List<AsistenciaDetalleDTO> obtenerAsistenciasPorFecha(LocalDate fecha);
}
