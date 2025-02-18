package com.example.iesiback.services;

import com.example.iesiback.entities.Cursada;
import com.example.iesiback.entities.Nota;
import com.example.iesiback.repositories.CursadaRepository;
import com.example.iesiback.services.CursadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CursadaServiceImpl implements CursadaService {

    @Autowired
    private CursadaRepository cursadaRepository;

    @Override
    public List<Cursada> getAllCursadas() {
        return cursadaRepository.findAll();
    }

    @Override
    public Optional<Cursada> getCursadaById(Integer id) {
        return cursadaRepository.findById(id);
    }

    @Override
    public Cursada saveCursada(Cursada cursada) {
        return cursadaRepository.save(cursada);
    }

    @Override
    public void deleteCursada(Integer id) {
        cursadaRepository.deleteById(id);
    }

    @Override
    public List<Cursada> findByLegajoId(String legajoId) {
        return cursadaRepository.findByLegajoId(legajoId);
    }

    public List<Cursada> getCursadasNoAprobadas(String legajoId) {
        List<Cursada> cursadas = cursadaRepository.findByLegajoId(legajoId);

        return cursadas.stream()
                .filter(cursada -> cursada.getNotas() != null && !cursada.getNotas().isEmpty()) // Asegura que tenga notas
                .filter(cursada -> cursada.getNotas().stream()
                        .noneMatch(nota -> "Aprobado".equalsIgnoreCase(nota.getNotaEstado()) ||
                                "Cursando".equalsIgnoreCase(nota.getNotaEstado()))) // Ninguna está aprobada ni cursando
                .collect(Collectors.toList());
    }


    public List<String> obtenerCorrelativasPendientes(Cursada cursada) {
        String correlativas = cursada.getCursadaMateriaCarrera().getMateria().getMateriaCursada();

        if (correlativas == null || correlativas.isEmpty() || correlativas.equals("-")) {
            return Collections.emptyList(); // No tiene correlativas, no debe ninguna
        }

        List<String> correlativasFaltantes = new ArrayList<>();
        String[] correlativasArray = correlativas.split("-"); // Obtener correlativas en orden

        for (String materiaOrden : correlativasArray) {
            Optional<Cursada> correlativaCursada = cursadaRepository.findByMateriaOrdenAndLegajoId(materiaOrden, cursada.getLegajo().getLegajoId());

            // Si la correlativa no existe o no tiene notas aprobadas, la añadimos a la lista de pendientes
            if (correlativaCursada.isEmpty() || !tieneNotaAprobada(correlativaCursada.get())) {
                correlativasFaltantes.add(materiaOrden);
            }
        }

        return correlativasFaltantes.isEmpty() ? Arrays.asList(correlativasArray) : correlativasFaltantes;
    }




    private boolean tieneNotaAprobada(Cursada cursada) {
        return cursada.getNotas().stream()
                .anyMatch(nota -> "Aprobada".equalsIgnoreCase(nota.getNotaEstado()));
    }


}
