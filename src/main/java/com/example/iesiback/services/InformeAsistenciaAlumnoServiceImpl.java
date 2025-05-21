package com.example.iesiback.services;

import com.example.iesiback.dto.InformeAsistenciaDTO;
import com.example.iesiback.entities.InformeAsistenciaAlumno;
import com.example.iesiback.repositories.InformeAsistenciaAlumnoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class InformeAsistenciaAlumnoServiceImpl implements InformeAsistenciaAlumnoService {


    private final InformeAsistenciaAlumnoRepository repository;

    public InformeAsistenciaAlumnoServiceImpl(InformeAsistenciaAlumnoRepository repository) {
        this.repository = repository;
    }

    @Override
    public InformeAsistenciaAlumno guardar(InformeAsistenciaAlumno informe) {
        return repository.save(informe);
    }

    @Override
    public InformeAsistenciaAlumno actualizar(Integer id, InformeAsistenciaAlumno informe) {
        informe.setIdInforme(id);
        return repository.save(informe);
    }

    @Override
    public void eliminar(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<InformeAsistenciaAlumno> obtenerPorId(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<InformeAsistenciaAlumno> listarTodos() {
        return repository.findAll();
    }


    @Override
    public List<InformeAsistenciaAlumno> buscarPorMateriaCarreraId(Integer id) {
        return repository.findByMateriaCarrera_Id(id);
    }




}