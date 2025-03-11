package com.example.iesiback.services;
import com.example.iesiback.entities.Cursada;
import com.example.iesiback.entities.Legajo;
import com.example.iesiback.entities.Materia;
import com.example.iesiback.entities.MateriaCarrera;
import com.example.iesiback.repositories.CursadaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CursadaServiceImpl implements CursadaService {

    private final MateriaCarreraService materiaCarreraService;
    @Autowired
    private CursadaRepository cursadaRepository;
    @Autowired
    public CursadaServiceImpl(CursadaRepository cursadaRepository,
                              MateriaCarreraService materiaCarreraService) { // ✅ Inyectar en el constructor
        this.cursadaRepository = cursadaRepository;
        this.materiaCarreraService = materiaCarreraService;
    }

    @Transactional
    @Override
    public void agregarMateriasACursadaPorCarrera(String carreraId, Legajo legajo) {
        List<MateriaCarrera> materias = materiaCarreraService.obtenerMateriasPorCarrera(carreraId);
        for (MateriaCarrera materia : materias) {
            if(materia.getMateria().getMateriaNivel().equals("1ro")) {
                Cursada nuevaCursada = new Cursada();
                nuevaCursada.setMateriaCarrera(materia);
                nuevaCursada.setLegajo(legajo);
                nuevaCursada.setCursadaInscripto(true);
                cursadaRepository.save(nuevaCursada);
            }

        }
    }



    @Override
    public List<Cursada> getAllCursadas() {
        return cursadaRepository.findAll();
    }

    @Override
    public Optional<Cursada> getCursadaById(Integer id) {
        return cursadaRepository.findById(id);
    }

    @Override
    public Optional<Cursada> obtenerCursadaPorLegajoMateriaCarrera(String legajoId, String materiaId, String carreraId) {
        return Optional.empty();
    }

    @Override
    public Optional<Cursada> findByLegajo_LegajoIdAndMateriaCarrera_Id(String legajoId, int materiaCarreraId) {
        return cursadaRepository.findByLegajo_LegajoIdAndMateriaCarrera_Id(legajoId, materiaCarreraId);
    }

    @Override
    public Cursada saveCursada(Cursada cursada) {
    //public Cursada saveCursada(String legajoId, String materiaCarreraId, Cursada cursada) {

        //Optional<Cursada> cursadaOpt = cursadaRepository.findByLegajo_LegajoIdAndMateriaCarrera_Id(legajoId, materiaCarreraId);
        Optional<Cursada> cursadaOpt = cursadaRepository.findByLegajo_LegajoIdAndMateriaCarrera_Id(cursada.getLegajo().getLegajoId(), cursada.getMateriaCarrera().getId());
        if (cursadaOpt.isPresent()) {
            Cursada cursadaExistente = cursadaOpt.get();
            cursadaExistente.setCursadaInscripto(!cursadaExistente.getCursadaInscripto());
            return cursadaRepository.save(cursadaExistente);
        } else {
            return cursadaRepository.save(cursada);

        }
    }



    @Override
    public void deleteCursada(Integer id) {
        cursadaRepository.deleteById(id);
    }


    @Override
    public List<Cursada> findByLegajoId(String legajoId) {
        return cursadaRepository.findByLegajoId(legajoId);
    }

    @Override
    public List<Cursada> findByLegajoAndMateria(String legajoId, String materiaId) {
        return cursadaRepository.findByLegajoAndMateria(legajoId, materiaId);
    }

    @Override
    public Optional<Boolean> obtenerEstadoCursada(String legajoId, String materiaId, String materiaYear) {
        return cursadaRepository.findEstadoByLegajoAndMateria(legajoId, materiaId, materiaYear);
    }
    @Override
    public List<Cursada> getCursadasNoAprobadas(String legajoId) {
        List<Cursada> cursadas = cursadaRepository.findByLegajoId(legajoId);

        return cursadas.stream()
                .filter(cursada -> cursada.getNotas() != null && !cursada.getNotas().isEmpty()) // Asegura que tenga notas
                .filter(cursada -> cursada.getNotas().stream()
                        .noneMatch(nota -> "Aprobado".equalsIgnoreCase(nota.getNotaEstado()) ||
                                "Cursando".equalsIgnoreCase(nota.getNotaEstado()))) // Ninguna está aprobada ni cursando
                .collect(Collectors.toList());
    }


    @Override
    public List<String> obtenerCorrelativasPendientes(Cursada cursada) {
        String correlativas = cursada.getMateriaCarrera().getMateria().getMateriaCursada();
        if (correlativas == null || correlativas.isEmpty() || correlativas.equals("-")) {
            return Collections.emptyList();
        }
        List<String> correlativasFaltantes = new ArrayList<>();
        String[] correlativasArray = correlativas.split("-");
        for (String materiaOrden : correlativasArray) {
            List<Cursada> correlativaCursadas = cursadaRepository.findByMateriaOrdenAndLegajoId(
                    String.valueOf(Integer.parseInt(materiaOrden)), cursada.getLegajo().getLegajoId());
            // Se considera aprobada si está en estado "Aprobada" o "Regular"
            boolean aprobadaORegular = correlativaCursadas.stream().anyMatch(this::tieneNotaAprobadaORegular);
            if (correlativaCursadas.isEmpty() || !aprobadaORegular) {
                correlativasFaltantes.add(materiaOrden);
            }
        }
        // Si no hay correlativas pendientes, se retorna la lista original de correlativas
        return correlativasFaltantes.isEmpty() ? Arrays.asList(correlativasArray) : correlativasFaltantes;
    }


    @Override
    public List<String> obtenerCorrelativasPendientesMateriaId(String legajoId, Materia materia) {

            String correlativas = materia.getMateriaCursada();
        if (correlativas == null || correlativas.isEmpty() || correlativas.equals("-")) {
            return Collections.emptyList();
        }
        List<String> correlativasFaltantes = new ArrayList<>();
        String[] correlativasArray = correlativas.split("-");
        for (String materiaOrden : correlativasArray) {
            List<Cursada> correlativaCursadas = cursadaRepository.findByMateriaOrdenAndLegajoId(
                    String.valueOf(Integer.parseInt(materiaOrden)),legajoId);
            boolean aprobadaORegular = correlativaCursadas.stream().anyMatch(this::tieneNotaAprobadaORegular);
            if (correlativaCursadas.isEmpty() || !aprobadaORegular) {
                correlativasFaltantes.add(materiaOrden);
            }
        }
        // Si no hay correlativas pendientes, se retorna la lista original de correlativas
        return correlativasFaltantes.isEmpty() ? Arrays.asList(correlativasArray) : correlativasFaltantes;
    }



    private boolean tieneNotaAprobadaORegular(Cursada cursada) {
        return cursada.getNotas().stream()
                .anyMatch(nota -> "Aprobada".equalsIgnoreCase(nota.getNotaEstado())
                        || "Regular".equalsIgnoreCase(nota.getNotaEstado()));
    }

@Transactional
@Override
public void eliminarCursada(Integer id) {
        Cursada cursada = cursadaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cursada no encontrada con ID: " + id));

        cursadaRepository.delete(cursada);
    }
}
