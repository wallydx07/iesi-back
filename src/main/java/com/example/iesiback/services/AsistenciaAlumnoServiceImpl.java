package com.example.iesiback.services;
import com.example.iesiback.dto.*;
import com.example.iesiback.entities.AsistenciaAlumno;
import com.example.iesiback.entities.InformeAsistenciaAlumno;
import com.example.iesiback.enums.EstadoAsistencia;
import com.example.iesiback.repositories.AsistenciaAlumnoRepository;
import com.example.iesiback.repositories.InformeAsistenciaAlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AsistenciaAlumnoServiceImpl implements AsistenciaAlumnoService {

    private final AsistenciaAlumnoRepository repository;
    @Autowired
    private InformeAsistenciaAlumnoRepository informeAsistenciaAlumnoRepository;

    public AsistenciaAlumnoServiceImpl(
            AsistenciaAlumnoRepository repository,
            InformeAsistenciaAlumnoRepository informeAsistenciaAlumnoRepository, InformeAsistenciaAlumnoRepository informeAsistenciaAlumnoRepository1) {
        this.repository = repository;
        this.informeAsistenciaAlumnoRepository = informeAsistenciaAlumnoRepository1;
    }

    @Override
    public List<AsistenciaAlumno> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<AsistenciaAlumno> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public AsistenciaAlumno save(AsistenciaAlumno asistenciaAlumno) {
        return repository.save(asistenciaAlumno);
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public List<AlumnoAsistenciaDTO> obtenerAsistenciasConDetalle(Integer idInforme) {
        List<AlumnoAsistenciaDTO> lista = repository.obtenerAsistenciasConDetalle(idInforme);
        lista.forEach(dto -> {
            if (dto.getEstado() == null) {
                dto.setEstado(EstadoAsistencia.AUSENTE);
            }
        });
        return lista;
    }

//    @Override
//    public void guardarTodas(List<AsistenciaAlumno> asistencias) {
//        repository.saveAll(asistencias);
//    }

    @Override
    @Transactional
    public void guardarTodas(List<AsistenciaAlumno> asistencias) {
        for (AsistenciaAlumno asistencia : asistencias) {
            System.out.println("Recibido id: " + asistencia.getId());
            System.out.println("Recibido idInforme: " + asistencia.getIdInforme().getIdInforme());
            System.out.println("Recibido legajoId: " + asistencia.getLegajoId());
            System.out.println("Recibido estado: " + asistencia.getEstado());
            if (asistencia.getIdInforme() != null && asistencia.getIdInforme().getIdInforme() != null) {
                Integer idInforme = asistencia.getIdInforme().getIdInforme();
                InformeAsistenciaAlumno informe = informeAsistenciaAlumnoRepository.findById(idInforme)
                        .orElseThrow(() -> new RuntimeException("Informe no encontrado con ID: " + idInforme));
                asistencia.setIdInforme(informe);
            } else {
                throw new RuntimeException("Falta el idInforme en una asistencia.");
            }
        }
        repository.saveAll(asistencias);
    }

    @Override
    public List<AsistenciaAlumnoDTO> obtenerAsistencias(String materiaCarreraId) {
        return repository.obtenerAsistenciaPorMateriaCarrera(materiaCarreraId);
    }

    @Override
    public List<InformeAsistenciaDTO> obtenerFechasAsistencia(String materiaCarreraId) {
        return repository.obtenerFechasAsistencia(materiaCarreraId);
    }

    @Override
    public List<AsistenciaResumenDTO> obtenerResumenAsistencia(String legajoId, int anioActual) {
        return repository.obtenerResumenAsistencia(legajoId, anioActual);
    }


    @Transactional(readOnly = true)
    @Override
    public List<AsistenciaAlumnoDetalleDTO> obtenerDetalleAsistencia(String legajoId, int anio) {
        return repository.obtenerDetalleAsistencia(legajoId, anio)
                .stream()
                .map(d -> d.estado() != null
                        ? d
                        : new AsistenciaAlumnoDetalleDTO(d.materiaId(), d.materiaNombre(), d.fecha(), EstadoAsistencia.AUSENTE))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Map<String, List<AsistenciaAlumnoDetalleDTO>> obtenerDetallePorMateria(String legajoId, int anio) {
        return obtenerDetalleAsistencia(legajoId, anio)
                .stream()
                .collect(Collectors.groupingBy(
                        AsistenciaAlumnoDetalleDTO::materiaId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }
}

