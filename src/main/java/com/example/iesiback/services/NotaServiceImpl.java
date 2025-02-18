package com.example.iesiback.services;

import com.example.iesiback.dto.NotaMateriaDTO;
import com.example.iesiback.entities.Nota;
import com.example.iesiback.repositories.NotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotaServiceImpl implements NotaService {  // ✅ Implementa la interfaz NotaService
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // ⚠️ Define el formato esperado

    @Autowired
    private NotaRepository notaRepository;

 //   @Override
  //  public List<Nota> obtenerNotas() {
  //      return notaRepository.findAllWithRelations();  // 🚀 Optimización aplicada

  //  }


  //  public List<Nota> obtenerNotasPorLegajo(String legajoId) {
  //      return notaRepository.findNotasByLegajoId(legajoId);
   // }

    @Override
    public List<Nota> obtenerNotas() {
        return List.of();
    }

    public List<NotaMateriaDTO> obtenerTodasNotasPorLegajo(String legajoId) {
        List<Object[]> resultados = notaRepository.findTodasNotasByLegajo(legajoId);

        return resultados.stream().map(obj -> {
            LocalDate fechaNota = null;
            try {
                if (obj[9] != null) {
                    fechaNota = LocalDate.parse(obj[9].toString(), formatter); // ✅ Usa el formato correcto
                }
            } catch (DateTimeParseException e) {
                System.err.println("Error al parsear la fecha: " + obj[9]); // Debugging en consola
            }

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
                    fechaNota,         // ✅ Fecha con formato corregido
                    (String) obj[10],  // nota_observaciones
                    (String) obj[11],  // nota_usuario
                    (String) obj[12]  // materia_id
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
                            (String) obj[12]   // materia_id
                    );
                }).collect(Collectors.toList());
    }

}

