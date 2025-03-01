package com.example.iesiback.services;

import com.example.iesiback.dto.NotaExamenDTO;
import com.example.iesiback.dto.NotaCursadaDTO;
import com.example.iesiback.dto.NotaMateriaDTO;
import com.example.iesiback.entities.Cursada;
import com.example.iesiback.entities.Nota;
import com.example.iesiback.exception.ResourceNotFoundException;
import com.example.iesiback.repositories.NotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotaServiceImpl implements NotaService {

    private final CursadaService cursadaService;

    @Autowired

    public NotaServiceImpl(
            CursadaService cursadaService) {
        this.cursadaService = cursadaService;
    }

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // ⚠️ Define el formato esperado

    @Autowired
    private NotaRepository notaRepository;


    @Override
    public List<Nota> obtenerNotas() {
        return List.of();
    }

    public List<String> correlativasCursadaId(int cursadaId) {
        Cursada cursada = cursadaService.getCursadaById(cursadaId).orElse(null);
        return cursadaService.obtenerCorrelativasPendientesMateriaId(cursada.getLegajo().getLegajoId(), cursada.getMateriaCarrera().getMateria());
    }


    public List<NotaMateriaDTO> obtenerTodasNotasPorLegajo(String legajoId) {
        List<Object[]> resultados = notaRepository.findTodasNotasByLegajo(legajoId);

        // 📌 Ajustar el formato de fecha según la entrada
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        return resultados.stream().map(obj -> {
            LocalDate fechaNota = null;

            // 📌 Intentar parsear la fecha en el formato correcto
            if (obj[9] != null) {
                try {
                    fechaNota = LocalDate.parse(obj[9].toString(), formatter);
                } catch (DateTimeParseException e) {
                    System.err.println("Error al parsear la fecha: " + obj[9]);
                }
            }

            // 📌 Obtener cursadaId correctamente
            Integer cursadaId = (obj[12] instanceof Integer) ? (Integer) obj[12] : null;
            List<String> correlativas = (cursadaId != null) ? correlativasCursadaId(cursadaId) : List.of();

            return new NotaMateriaDTO(
                    (Integer) obj[0],  // nota_id
                    (Integer) obj[1],  // materia_orden
                    (String) obj[2],   // materia_nombre
                    (String) obj[3],   // nota_calificacion_nota_numero
                    (String) obj[4],   // nota_calificacion_nota_letra
                    (String) obj[5],   // nota_condicion
                    (String) obj[6],   // nota_estado
                    (String) obj[7],   // nota_libro_nota
                    (String) obj[8],   // nota_folio_nota
                    fechaNota,         // ✅ Fecha corregida
                    (obj[10] != null) ? obj[10].toString() : null,  // nota_observaciones (evita ClassCastException)
                    (obj[11] != null) ? obj[11].toString() : null,  // nota_usuario

                    "---",             // nota_status (asumí que es un placeholder)
                    correlativas,      // 📌 Lista de correlativas corregida
                    (obj[13] != null) ? obj[13].toString() : null   // materia_id (evita ClassCastException)
            );
        }).collect(Collectors.toList());
    }


    public List<NotaMateriaDTO> obtenerNotasNoAprobadasPorLegajo(String legajoId) {
        List<Object[]> resultados = notaRepository.findTodasNotasByLegajo(legajoId);

        return resultados.stream()
                .filter(obj -> {
                    String estadoNota = (String) obj[6]; // Columna que representa el estado de la nota
                    return !estadoNota.equalsIgnoreCase("Aprobado")
                            && !estadoNota.equalsIgnoreCase("Cursando");
                })
                .map(obj -> {
                    LocalDate fechaNota = null;
                    try {
                        if (obj[9] != null) {
                            fechaNota = LocalDate.parse(obj[9].toString(), formatter);
                        }
                    } catch (DateTimeParseException e) {
                        System.err.println("Error al parsear la fecha: " + obj[9]);
                    }
                    // 📌 Obtener cursadaId correctamente
                    Integer cursadaId = (obj[12] != null) ? (Integer) obj[12] : null; // Asegúrate de que el índice 12 es correcto
                    List<String> correlativas = (cursadaId != null) ? correlativasCursadaId(cursadaId) : List.of(); // Evitar `null`

                    return new NotaMateriaDTO(
                            (Integer) obj[0],  // nota_id
                            (Integer) obj[1],  // materia_orden
                            (String) obj[2],   // materia_nombre
                            (String) obj[3],   // nota_calificacion_nota_numero
                            (String) obj[4],   // nota_calificacion_nota_letra
                            (String) obj[5],   // nota_condicion
                            (String) obj[6],   // nota_estado
                            (String) obj[7],   // nota_libro_nota
                            (String) obj[8],   // nota_folio_nota
                            fechaNota,         // Fecha formateada
                            (String) obj[10],  // nota_observaciones
                            (String) obj[11],  // nota_usuario
                            "---",             // nota_status (asumí que no se usa en la consulta)
                            correlativas,      // 📌 Lista de correlativas corregida
                            (String) obj[12]   // materia_id
                    );
                }).collect(Collectors.toList());
    }
    @Override
    public boolean isMateriaAprobada(String legajoId, String materiaId) {
        List<Object[]> resultados = notaRepository.findTodasNotasByLegajo(legajoId);
        return resultados.stream()
                .anyMatch(obj -> {
                    String thisMateriaId = (String) obj[13];
                    String estadoNota = (String) obj[6];
                    return thisMateriaId != null
                            && thisMateriaId.equals(materiaId)
                            && "Aprobado".equalsIgnoreCase(estadoNota);
                });
    }



    @Override
    public List<NotaCursadaDTO> findNotasByCarreraAndMateria(String carreraId, String materaId,boolean cursadaInscripto) {
        List<NotaCursadaDTO> todasLasNotas = notaRepository.findNotasByCarreraAndMateria(carreraId, materaId,cursadaInscripto);
        return todasLasNotas;
    }

    @Override
    public List<NotaExamenDTO> findExamenesByCursadaExamenIdMateriaCarrera(
            Long  cursadaExamenId, Boolean examenInscripto) {
        List<NotaExamenDTO> todosLosExamenes = notaRepository.findExamenesByCursadaExamenIdMateriaCarrera(cursadaExamenId, examenInscripto);
        return todosLosExamenes;
    }


    @Override
    public Nota guardarNota(Nota nota) {
        return notaRepository.save(nota);
    }

    @Override
    public Nota obtenerNotaPorId(Long notaId) {
        return notaRepository.findById(notaId).orElse(null);
    }

    @Override
    public Nota actualizarNota(Long id, Nota nota) throws ResourceNotFoundException {
        // Verifica si existe la nota en la base de datos
        Nota notaExistente = notaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota no encontrada para el id :: " + id));

        // Actualiza los campos de la nota existente (opcional: puedes actualizar campo por campo)
        notaExistente.setNotaCalificacionNotaNumero(nota.getNotaCalificacionNotaNumero());
        notaExistente.setNotaCalificacionNotaLetra(nota.getNotaCalificacionNotaLetra());
        notaExistente.setNotaEstado(nota.getNotaEstado());
        notaExistente.setNotaLibroNota(nota.getNotaLibroNota());
        notaExistente.setNotaFolioNota(nota.getNotaFolioNota());
        notaExistente.setNotaFechaNota(nota.getNotaFechaNota());
        notaExistente.setNotaObservaciones(nota.getNotaObservaciones());

        // Guarda la nota actualizada y la retorna
        return notaRepository.save(notaExistente);
    }
}

