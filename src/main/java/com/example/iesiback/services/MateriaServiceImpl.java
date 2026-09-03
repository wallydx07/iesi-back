package com.example.iesiback.services;
import com.example.iesiback.dto.MateriaDTO;
import com.example.iesiback.dto.ReinscripcionMateriaDTO;
import com.example.iesiback.entities.Carrera;
import com.example.iesiback.entities.Materia;
import com.example.iesiback.repositories.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.iesiback.controllers.PagoController.logger;

@Service
public class MateriaServiceImpl implements MateriaService {


    public MateriaServiceImpl() {

    }

    @Override
    public Materia findMateriaById(String materiaId) {
        Optional<Materia> materiaOpt = materiaRepository.findById(materiaId);
        return materiaOpt.orElse(null);
    }

    @Autowired
    private MateriaRepository materiaRepository;

    @Override
    public List<Materia> obtenerTodasMaterias() {
        return materiaRepository.findAll();
    }

    @Override
    public Optional<Materia> obtenerMateriaPorId(String id) {
        return materiaRepository.findById(id);
    }

    @Override
    public Materia guardarMateria(Materia materia) {
        return materiaRepository.save(materia);
    }

    @Override
    public Materia actualizarMateria(String id, Materia materia) {
        Optional<Materia> materiaExistente = materiaRepository.findById(id);
        if (materiaExistente.isPresent()) {
            materia.setMateriaId(id); // Mantener el mismo ID
            return materiaRepository.save(materia);
        }
        return null; // O manejar con una excepción
    }

    @Override
    public void eliminarMateria(String id) {
        materiaRepository.deleteById(id);
    }

    @Override
    public List<MateriaDTO> obtenerMateriasPorCarrera(String carreraId) {
        return materiaRepository.findMateriasByCarrera(carreraId);
    }

    public Materia FindByNombreandCarrera(Carrera carrera, String Nombre){
        return null;
    }

    @Override
    public Optional<Materia> obtenerMateriaPorOrden(Integer orden) {
        return materiaRepository.findByMateriaOrden(orden);
    }

    @Override
    public List<ReinscripcionMateriaDTO> findReinscripciones(Integer cicloLectivo, String carreraNombre, String division) {
        return materiaRepository.findReinscripciones(cicloLectivo, carreraNombre,division);
    }

    @Override
    public List<Materia> findAllByIds(Set<String> ids) {
        return materiaRepository.findAllByMateriaIdIn(ids);
    }

//
//    public List<String> correlativasCursadaId(int cursadaId) {
//        Cursada cursada = cursadaService.getCursadaById(cursadaId).orElse(null);
//        return cursadaService.obtenerCorrelativasPendientes(cursada);
//    }

@Override
public Optional<Materia> obtenerMateriaPorNotaId(Long notaId) {
    return materiaRepository.findMateriaByNotaIdNativo(notaId);
}

    @Override
    public List<ReinscripcionMateriaDTO> obtenerMateriasPorCarreraYCurso(String carreraNombre, String nivel) {

        logger.info("Obteniendo materias por carrera y nivel. carreraNombre='{}', nivel='{}'",
                carreraNombre, nivel);

        int anioActual = LocalDate.now().getYear();

        logger.info("Año actual utilizado para la consulta: {}", anioActual);

        List<ReinscripcionMateriaDTO> todas =
                materiaRepository.findReinscripciones(anioActual, carreraNombre, "");

        logger.info("Materias obtenidas del repositorio: {}", todas.size());

//        List<ReinscripcionMateriaDTO> resultado = todas.stream()
//                .filter(m -> nivel.trim().equalsIgnoreCase(m.getMateriaNivel()))
//                .toList();

//        logger.info("Materias después de filtrar por nivel '{}': {}", nivel, resultado.size());

        return todas;
    }
}