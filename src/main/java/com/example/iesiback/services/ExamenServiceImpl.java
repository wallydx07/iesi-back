package com.example.iesiback.services;

import com.example.iesiback.dto.InscripcionExamenDTO;
import com.example.iesiback.entities.Cursada;
import com.example.iesiback.entities.Examen;
import com.example.iesiback.entities.Nota;
import com.example.iesiback.repositories.CursadaRepository;
import com.example.iesiback.repositories.ExamenRepository;
import com.example.iesiback.repositories.NotaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamenServiceImpl implements ExamenService {

    @Autowired
    private ExamenRepository examenRepository; // ✅ Se mantiene ExamenRepository

    @Autowired
    private NotaRepository notaRepository;

    @Autowired
    private CursadaRepository cursadaRepository;

    @Transactional
    public List<InscripcionExamenDTO> completarCursadas(String legajoId, String turno) {

        List<Cursada> cursadas = cursadaRepository.findByCursadaLegajoId(legajoId);


















        List<Object[]> resultados = notaRepository.findTodasNotasByLegajo(legajoId);
        List<InscripcionExamenDTO> listaNotas = resultados.stream().map(fila -> {
            LocalDate fechaNota = null;
            try {
                if (fila[9] != null) {
                    fechaNota = LocalDate.parse(fila[9].toString());
                }
            } catch (DateTimeParseException e) {
                System.err.println("Error al parsear la fecha: " + fila[9]);
            }

            return new InscripcionExamenDTO(
                    (Integer) fila[0],  // nota_id
                    (Integer) fila[1],  // materia_orden
                    (String) fila[2],   // materia_nombre
                    (String) fila[3],   // nota_calificacion_nota_numero
                    (String) fila[4],   // nota_calificacion_nota_letra
                    (String) fila[5],   // nota_condicion
                    (String) fila[6],   // nota_estado
                    (String) fila[7],   // nota_libro_nota
                    (String) fila[8],   // nota_folio_nota
                    fechaNota,          // fecha_nota
                    (String) fila[10],  // nota_observaciones
                    (String) fila[11],  // nota_usuario
                    (String) fila[12]   // materia_id
            );
        }).collect(Collectors.toList());

        return listaNotas;
    }











}
