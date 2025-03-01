package com.example.iesiback.services;


import com.example.iesiback.dto.MateriaDTO;
import com.example.iesiback.dto.ProcesadoReinscripcionMateriaDTO;
import com.example.iesiback.dto.ReinscripcionMateriaDTO;
import com.example.iesiback.entities.Materia;
import com.example.iesiback.repositories.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MateriaServiceImpl implements MateriaService {

    private final NotaService notaService;
    private final CursadaService cursadaService;
    private final InscripcionService inscripcionService;

    @Autowired
    public MateriaServiceImpl(
            NotaService notaService,
            CursadaService cursadaService, InscripcionService inscripcionService) {
        this.notaService = notaService;
        this.cursadaService= cursadaService;
        this.inscripcionService = inscripcionService;
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


    @Override
    public List<ProcesadoReinscripcionMateriaDTO> obtenerReinscripciones(Integer cicloLectivo,String legajoId) {

        List<ReinscripcionMateriaDTO> reinscripciones = new ArrayList<>();
        List<ProcesadoReinscripcionMateriaDTO> procesados = new ArrayList<>();
        String carreraNombre = inscripcionService.findByLegajoId(legajoId).getCarrera().getCarreraNombre();

       reinscripciones=materiaRepository.findReinscripciones(cicloLectivo, carreraNombre);

        for (ReinscripcionMateriaDTO dto : reinscripciones) {
          if(!notaService.isMateriaAprobada(legajoId,dto.getMateriaId())) {
              ProcesadoReinscripcionMateriaDTO procesado = new ProcesadoReinscripcionMateriaDTO();
              procesado.setMateriaId(dto.getMateriaId());
              procesado.setMateriaOrden(dto.getMateriaOrden());
              procesado.setMateriaNivel(dto.getMateriaNivel());
              procesado.setMateriaNombre(dto.getMateriaNombre());
              procesado.setCarreraNombre(dto.getCarreraNombre());
              procesado.setCarreraYear(dto.getCarreraYear());
              procesado.setMateriaCarreraId(dto.getMateriaCarreraId());
              System.out.println("--------------------------------------------LegajoID" + legajoId + "------MateriaId" + dto.getMateriaId());
              Materia materia=this.findMateriaById(dto.getMateriaId());
              List<String> correlativas = cursadaService.obtenerCorrelativasPendientesMateriaId(legajoId, materia);
              System.out.println("--------------------------------------------" + correlativas.size());
              String C = "";
              for (String aux : correlativas) {
                  C = C + aux + ",";
              }
                  Optional<Boolean> estadoOpt = cursadaService.obtenerEstadoCursada(legajoId, dto.getMateriaId(), String.valueOf(dto.getCarreraYear()));

                  if (estadoOpt.isPresent()) {
                      Boolean estado = estadoOpt.get();
                      procesado.setCursadaInscripto(estado);
                  } else {
                      procesado.setCursadaInscripto(false);
                  }
                  procesado.setMateriaCorrelativas(C);//correlativas pendientes
                  procesados.add(procesado);
              }
          }



        return procesados;
    }

/*
    public List<String> correlativasCursadaId(int cursadaId) {
        Cursada cursada = cursadaService.getCursadaById(cursadaId).orElse(null);
        return cursadaService.obtenerCorrelativasPendientes(cursada);
    }
    */
}