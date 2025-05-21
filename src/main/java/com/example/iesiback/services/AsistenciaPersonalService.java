package com.example.iesiback.services;

import com.example.iesiback.entities.AsistenciaPersonal;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AsistenciaPersonalService {
    List<AsistenciaPersonal> obtenerTodas();
    Optional<AsistenciaPersonal> buscarPorId(Integer id);
    AsistenciaPersonal guardar(AsistenciaPersonal asistencia);
    void eliminar(Integer id);

    boolean verificarSiYaEstaRegistrado(LocalDate fecha, Long dni, Integer horarioId);
    void registrarEntrada(LocalDate fecha, Long dni, Integer horarioId);

    boolean marcarHoraSalida(LocalDate fecha, Long dni, Integer horarioId);

    boolean AsistenciaDahua(String dni);
}
