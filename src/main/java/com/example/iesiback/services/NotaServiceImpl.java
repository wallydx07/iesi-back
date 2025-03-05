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
import java.util.*;
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
                    (obj[13] != null) ? obj[13].toString() : null  , // materia_id (evita ClassCastException)//aca decia 13
                    (String) obj[14]   // materia_nivel
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
                            (String) obj[12] ,  // materia_id
                            (String) obj[13]   // materia_nivel
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
        List<NotaCursadaDTO> todasLasNotas = notaRepository.findNotasByCarreraAndMateria(carreraId, materaId,cursadaInscripto,"Cursada");
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

    @Override
    public List<NotaMateriaDTO> obtenerTodasNotasPorLegajoAnalitico(String legajoId) {
        List<NotaMateriaDTO> notasRefinadas = new ArrayList<>();
        List<NotaMateriaDTO> notasOrigen    = new ArrayList<>();
        notasOrigen = this.obtenerTodasNotasPorLegajo(legajoId);

        boolean checkCorrelativas = true;//si es verdadero va a analizar las correlativas, caso contrario no analiza correlativas


        for (NotaMateriaDTO nota : notasOrigen) {

            if (nota.getNotaEstado().equals("Desaprobado")) {
                nota.setNotaEstado("Desaprobado");
            } else if (nota.getNotaEstado().endsWith("Cursando")) {
                LocalDate fechaNota = nota.getNotaFecha(); // Asegúrate de que es LocalDate
                int anioNota = fechaNota.getYear();
                int anioActual = LocalDate.now().getYear();
                if (anioNota == anioActual) {
                    nota.setNotaFinal("Cursando");
                } else {
                    nota.setNotaFinal("(-)");
                }
            } else if (nota.getNotaEstado().endsWith("Regular")) {
                nota.setNotaFinal("Regular");
            } else if (nota.getNotaEstado().endsWith("Libre")) {
                nota.setNotaFinal("Desaprobado");
            } else if (nota.getNotaEstado().endsWith("Ausente")) {
                nota.setNotaFinal("Desaprobado");
            } else {
                nota.setNotaFinal(nota.getNotaCalificacionNumero() + "(" + nota.getNotaCalificacionLetra() + ")");
            }

            if (buscarClaveAnalitico(notasRefinadas, nota)) {
                notasRefinadas = validadorAnalitico(notasRefinadas, nota);
            } else {
                notasRefinadas.add(nota);
            }
        }

        if (checkCorrelativas) {

            for (NotaMateriaDTO notas : notasRefinadas) {
                List<String> correlativas=notas.getCorrelativas();
                for (String numero : correlativas) {
                    if (!correlativas(Integer.parseInt(numero), notasRefinadas, "aprobado")) {
                        notas.setNotaFinal("(-)");  //dESACTIVAR PARA VER CSIN CORRELATIVAS===========================
                        break;
                    }
                }
            }

        }
        return notasRefinadas;
    }


    public boolean correlativas(int materia_orden, List<NotaMateriaDTO> analitico, String tipo) {
        boolean aux = false;
        System.out.println("+materiaOrden" + materia_orden);
        System.out.println("Tamaño del linkedlist" + analitico.size());
        if (analitico != null && !analitico.isEmpty() && materia_orden >= 0 && materia_orden < analitico.size()) {
            NotaMateriaDTO xd = analitico.get(materia_orden - 1);
            if (xd != null) {
                String cond = xd.getNotaEstado() != null ? xd.getNotaEstado() : "";
                String valorString = xd.getNotaCalificacionNumero()!= null ? xd.getNotaCalificacionNumero() : "";
                System.out.println("Nota numero" + valorString + " materia: " + xd.getMateriaNombre() + " notaletra " + xd.getNotaCalificacionLetra());
                double nota;
                try {
                    nota = Double.parseDouble(valorString);
                } catch (NumberFormatException e) {
                    // En caso de error, establecer el valor en 1
                    nota = 1.0;
                }
                switch (tipo) {
                    case "regular":
                        if (nota >= 4) {
                            System.out.println("Correlativa aceptada " + materia_orden + "nota: " + nota);
                            aux = true;
                        } else {
                            System.out.println("Correlativa rechazada " + materia_orden + "nota: " + nota);
                        }
                        break;
                    case "aprobado":
                        if (nota >= 4 && cond.equals("Aprobado")) {
                            aux = true;
                            System.out.println("Correlativa aprobada " + materia_orden + "nota: " + nota);
                        } else {
                            System.out.println("Correlativa desaprobada " + materia_orden + "nota: " + nota + " Cond: " + cond);
                        }
                        break;
                }
            } else {
                System.out.println("El objeto xd no tiene la estructura esperada.");
            }
        } else {
            System.out.println("Los datos de entrada no son válidos.");
        }

        return aux;
    }

public boolean buscarClaveAnalitico(List<NotaMateriaDTO> analitico, NotaMateriaDTO materia) {
    return analitico.stream()
            .anyMatch(xd -> Objects.equals(xd.getMateriaNombre(), materia.getMateriaNombre()));
}


    public List<NotaMateriaDTO> validadorAnalitico(List<NotaMateriaDTO> analitico, NotaMateriaDTO materia) {
        List<NotaMateriaDTO> nuevalista = new ArrayList<>();
        System.out.println(".-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.");
        for (NotaMateriaDTO xd : analitico) {
            System.out.println("_________________________________________________________________________");
           // System.out.println("materias son " + xd.getMateriaNombre() + "|" + materia.getNombre());

            if (xd.getMateriaNombre().equals(materia.getMateriaNombre())) {
                // Si las materias se repiten
                String cond1 = xd.getNotaEstado();
                String cond2 = materia.getNotaEstado();
                System.out.println("condicion 1: " + cond1 + " | condicion 2: " + cond2);

                if (cond1.equals("Aprobado")) {
                    // Si la primera materia está aprobada, no hacer nada y conservar su estado
                    System.out.println("La primera materia está aprobada, se conserva");
                    nuevalista.add(xd);
                } else if (cond2.equals("Aprobado")) {
                    // Si la segunda materia está aprobada, actualizar y conservar su estado
                    System.out.println("La segunda materia está aprobada, se reemplaza");
                    nuevalista.add(materia);
                } else if (cond1.equals("Cursando") && (cond2.equals("Regular") || cond2.equals("Libre") || cond2.equals("Desaprobado") || cond2.equals("Ausente"))) {
                    // La primera materia está cursando y la segunda está en estado regular o libre, actualizar a cursando
                    System.out.println("La primera materia está cursando y la segunda está en estado regular o libre, se conserva");
                    nuevalista.add(xd);
                } else if (cond1.equals("Regular") && cond2.equals("Regular")) {
                    // Ambas materias están regulares, actualizar a la última
                    System.out.println("Ambas materias están regulares, se reemplaza");
                    nuevalista.add(materia);
                } else if (cond1.equals("Desaprobado") && cond2.equals("Desaprobado")) {
                    // Ambas materias están desaprobadas, actualizar a la última
                    System.out.println("Ambas materias están desaprobadas, se reemplaza");
                    nuevalista.add(materia);
                } else if (cond1.equals("Regular") && cond2.equals("Desaprobado")) {
                    // La primera materia está en estado regular y la segunda está desaprobada, conservar el estado de la primera
                    System.out.println("La primera materia está regular y la segunda está desaprobada, se conserva");
                    nuevalista.add(xd);
                } else if (cond1.equals("Regular") && cond2.equals("Cursando")) {
                    // La primera materia está en estado regular y la segunda está cursando, conservar el estado de la primera
                    System.out.println("La primera materia está regular y la segunda está cursando, se conserva");
                    nuevalista.add(xd);
                } else if (cond1.equals("Regular") && cond2.equals("Ausente")) {
                    // La primera materia está en estado regular y la segunda está cursando, conservar el estado de la primera
                    System.out.println("La primera materia está regular y la segunda está Ausente, se conserva");
                    nuevalista.add(xd);
                } else if (cond1.equals("Desaprobado") && cond2.equals("Regular")) {
                    // La primera materia está desaprobada y la segunda está en estado regular, actualizar a regular
                    System.out.println("La primera materia está desaprobada y la segunda está regular, se reemplaza");
                    nuevalista.add(materia);
                } else if (cond1.equals("Desaprobado") && cond2.equals("Cursando")) {
                    // La primera materia está desaprobada y la segunda está cursando, conservar el estado de la primera
                    System.out.println("La primera materia está desaprobada y la segunda está cursando, se reemplaza");
                    nuevalista.add(materia);
                } else if (cond1.equals("Libre") && cond2.equals("Cursando")) {
                    // La primera materia está libre y la segunda está cursando, conservar el estado de la primera
                    System.out.println("La primera materia está libre y la segunda está cursando, se reemplaza");
                    nuevalista.add(materia);
                } else if (cond1.equals("Libre") && cond2.equals("Ausente")) {
                    // La primera materia está libre y la segunda está cursando, conservar el estado de la primera
                    System.out.println("La primera materia está libre y la segunda está Ausente, se conserva");
                    nuevalista.add(xd);
                } else if (cond1.equals("Ausente") && cond2.equals("Libre")) {
                    // La primera materia está libre y la segunda está cursando, conservar el estado de la primera
                    System.out.println("La primera materia está Ausente y la segunda está Libre, se reemplaza");
                    nuevalista.add(materia);
                } else if (cond1.equals("Ausente") && cond2.equals("Regular")) {
                    // La primera materia está libre y la segunda está cursando, conservar el estado de la primera
                    System.out.println("La primera materia está Ausente y la segunda está Libre, se reemplaza");
                    nuevalista.add(materia);
                } else {
                    // Agregar cualquier otra combinación de estados
                    System.out.println("Combinación de estados no contemplada, se conserva");
                    nuevalista.add(xd);
                }
            } else {
                nuevalista.add(xd);
            }
        }

        return nuevalista;
    }
}

