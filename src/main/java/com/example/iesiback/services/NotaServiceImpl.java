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
import org.springframework.transaction.annotation.Transactional;

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

//    public List<String> correlativasCursadaId(int cursadaId) {
//        Cursada cursada = cursadaService.getCursadaById(cursadaId).orElse(null);
//        return cursadaService.obtenerCorrelativasPendientesMateriaId(cursada.getLegajo().getLegajoId(), cursada.getMateriaCarrera().getMateria());
//    }




        public String correlativasCursadaId(int cursadaId) {
        Cursada cursada = cursadaService.getCursadaById(cursadaId).orElse(null);
        return cursadaService.obtenerCorrelativasPendientes(cursada.getMateriaCarrera().getMateria().getMateriaId(), cursada.getLegajo().getLegajoId());
    }




    public List<NotaMateriaDTO> mapResultsToDTO(List<Object[]> results) {
        List<NotaMateriaDTO> dtoList = new ArrayList<>();
        for (Object[] row : results) {
            NotaMateriaDTO dto = new NotaMateriaDTO();
            dto.setNotaId((Integer) row[0]);
            dto.setMateriaOrden((Integer) row[1]);
            dto.setMateriaNombre((String) row[2]);
            dto.setNotaCalificacionNumero((String) row[3]);
            dto.setNotaCalificacionLetra((String) row[4]);
            dto.setNotaCondicion((String) row[5]);
            dto.setNotaEstado((String) row[6]);
            dto.setNotaLibro((String) row[7]);
            dto.setNotaFolio((String) row[8]);
            LocalDate fecha =formatearFecha((String) row[9]);
            dto.setNotaFecha(fecha);
            dto.setNotaObservaciones((String) row[10]);
            dto.setNotaUsuario((String) row[11]);
            dto.setCursadaId((Integer) row[12]);
            dto.setMateriaId((String) row[13]);
            dto.setMateriaNivel((String) row[14]);
            dtoList.add(dto);
        }
        return dtoList;
    }

    public static LocalDate formatearFecha(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) {
            return null;
        }
        List<DateTimeFormatter> formatos = List.of(
                DateTimeFormatter.ISO_LOCAL_DATE,  // yyyy-MM-dd
                DateTimeFormatter.ofPattern("dd/MM/yyyy"), // dd/MM/yyyy
                DateTimeFormatter.ofPattern("dd-MM-yyyy")  // dd-MM-yyyy
        );
        for (DateTimeFormatter formato : formatos) {
            try {
                return LocalDate.parse(fecha, formato);
            } catch (DateTimeParseException ignored) {
            }
        }
        System.err.println("Error al formatear la fecha: " + fecha);
        return null;
    }



//    public List<NotaMateriaDTO> obtenerTodasNotasPorLegajo(String legajoId) {
//        List<Object[]> results = notaRepository.findNotasPorLegajo(legajoId);
//        System.out.println(results.size());
//        List<NotaMateriaDTO> resultados=mapResultsToDTO(results);
//        return resultados.stream()
//                .filter(Objects::nonNull) // Asegurarse de que no sea null
//                .map(obj -> {
//                    Integer cursadaId = obj.getCursadaId();
//                    List<String> correlativas = (cursadaId != null) ? correlativasCursadaId(cursadaId) : List.of();
//                    obj.setCorrelativas(correlativas);
//                    return obj;
//                })
//                .collect(Collectors.toList());
//    }

    public List<NotaMateriaDTO> obtenerTodasNotasPorLegajo(String legajoId) {
        List<Object[]> results = notaRepository.findNotasPorLegajo(legajoId);
        System.out.println(results.size());
        List<NotaMateriaDTO> resultados = mapResultsToDTO(results);

        return resultados.stream()
                .filter(Objects::nonNull) // Asegurarse de que no sea null
                .map(obj -> {
                    Integer cursadaId = obj.getCursadaId();
                    List<String> correlativas = (cursadaId != null) ?
                            // Convertir el string separado por comas en una lista de String
                            Arrays.asList(correlativasCursadaId(cursadaId).split(",")) :
                            List.of();
                    obj.setCorrelativas(correlativas);
                    return obj;
                })
                .collect(Collectors.toList());
    }



//    public List<NotaMateriaDTO> obtenerNotasNoAprobadasPorLegajo(String legajoId) {
//        List<Object[]> results = notaRepository.findNotasPorLegajo(legajoId);
//        List<NotaMateriaDTO> resultados=mapResultsToDTO(results);
//        return resultados.stream()
//                .filter(obj -> {
//                    String estadoNota = obj.getNotaEstado(); // Acceso directo al estado de la nota
//                    return !estadoNota.equalsIgnoreCase("Aprobado")
//                            && !estadoNota.equalsIgnoreCase("Cursando");
//                })
//                .map(obj -> {
//                    // 📌 Manejamos la fecha de forma más segura
//                    LocalDate fechaNota = null;
//                    try {
//                        if (obj.getNotaFecha() != null) {
//                            fechaNota = obj.getNotaFecha(); // Ya es un objeto LocalDate, no necesitamos parsear
//                        }
//                    } catch (DateTimeParseException e) {
//                        System.err.println("Error al parsear la fecha: " + obj.getNotaFecha());
//                    }
//
//                    // 📌 Obtener cursadaId correctamente
//                    Integer cursadaId = obj.getCursadaId(); // Obtención directa de cursadaId
//                    List<String> correlativas = (cursadaId != null) ? correlativasCursadaId(cursadaId) : List.of(); // Evitar null
//
//                    // Retornar el objeto NotaMateriaDTO con las correlativas
//                    obj.setCorrelativas(correlativas); // Seteamos las correlativas en el mismo objeto
//
//                    return obj;
//                }).collect(Collectors.toList());
//    }
public List<NotaMateriaDTO> obtenerNotasNoAprobadasPorLegajo(String legajoId) {
    List<Object[]> results = notaRepository.findNotasPorLegajo(legajoId);
    List<NotaMateriaDTO> resultados = mapResultsToDTO(results);

    return resultados.stream()
            .filter(obj -> {
                String estadoNota = obj.getNotaEstado(); // Acceso directo al estado de la nota
                return !estadoNota.equalsIgnoreCase("Aprobado")
                        && !estadoNota.equalsIgnoreCase("Cursando");
            })
            .map(obj -> {
                // 📌 Manejamos la fecha de forma más segura
                LocalDate fechaNota = null;
                try {
                    if (obj.getNotaFecha() != null) {
                        fechaNota = obj.getNotaFecha(); // Ya es un objeto LocalDate, no necesitamos parsear
                    }
                } catch (DateTimeParseException e) {
                    System.err.println("Error al parsear la fecha: " + obj.getNotaFecha());
                }

                // 📌 Obtener cursadaId correctamente
                Integer cursadaId = obj.getCursadaId(); // Obtención directa de cursadaId
                List<String> correlativas = (cursadaId != null) ?
                        // Convertir el string separado por comas en una lista de String
                        Arrays.asList(correlativasCursadaId(cursadaId).split(",")) :
                        List.of(); // Evitar null

                // Retornar el objeto NotaMateriaDTO con las correlativas
                obj.setCorrelativas(correlativas); // Seteamos las correlativas en el mismo objeto

                return obj;
            }).collect(Collectors.toList());
}


    @Override
    public boolean isMateriaAprobada(String legajoId, String materiaId) {
        List<Object[]> results = notaRepository.findNotasPorLegajo(legajoId);
        List<NotaMateriaDTO> resultados=mapResultsToDTO(results);

        return resultados.stream()
                .anyMatch(obj -> {
                    String thisMateriaId = obj.getMateriaId(); // Accedemos directamente al materiaId
                    String estadoNota = obj.getNotaEstado(); // Accedemos al estado de la nota
                    return thisMateriaId != null
                            && thisMateriaId.equals(materiaId)
                            && "Aprobado".equalsIgnoreCase(estadoNota);
                });
    }


    @Override
    public List<NotaCursadaDTO> findNotasByCarreraAndMateria(String carreraId, String materaId, boolean cursadaInscripto) {
        List<NotaCursadaDTO> todasLasNotas = notaRepository.findNotasByCarreraAndMateria(carreraId, materaId, cursadaInscripto, "Cursada");
        return todasLasNotas;
    }

    @Override
    public List<NotaExamenDTO> findExamenesByCursadaExamenIdMateriaCarrera(
            Long cursadaExamenId, Boolean examenInscripto) {
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


//
//    @Override
//    public List<NotaMateriaDTO> obtenerTodasNotasPorLegajoAnalitico(String legajoId) {
//        List<NotaMateriaDTO> notasOrigen = this.obtenerTodasNotasPorLegajo(legajoId);
//        List<NotaMateriaDTO> notasRefinadas = new ArrayList<>();
//        Set<Integer> materiasProcesadas = new HashSet<>(); // Almacena los órdenes de materias ya procesadas
//        boolean checkCorrelativas = true;
//        for (NotaMateriaDTO nota : notasOrigen) {
//            nota.setNotaFinal(definirNotaFinal(nota));
//            int ordenMateria = nota.getMateriaOrden(); // Suponiendo que hay un campo que indica el orden de la materia
//            if (materiasProcesadas.contains(ordenMateria)) {
//                notasRefinadas = validadorAnalitico(notasRefinadas, nota);
//            } else {
//                materiasProcesadas.add(ordenMateria);
//                notasRefinadas.add(nota);
//            }
//        }
//        if (checkCorrelativas) {
//            validarCorrelativas(notasRefinadas);
//        }
//        return notasRefinadas;
//    }


    @Override
    public List<NotaMateriaDTO> obtenerTodasNotasPorLegajoAnalitico(String legajoId) {
        List<NotaMateriaDTO> notasOrigen = this.obtenerTodasNotasPorLegajo(legajoId);
        Map<Integer, NotaMateriaDTO> materiasMap = new HashMap<>(); // Evita duplicados y almacena la mejor nota
        boolean checkCorrelativas = true;
        for (NotaMateriaDTO nota : notasOrigen) {
            nota.setNotaFinal(definirNotaFinal(nota));
            int ordenMateria = nota.getMateriaOrden();
            if (materiasMap.containsKey(ordenMateria)) {
                NotaMateriaDTO mejorNota = validadorAnalitico(materiasMap.get(ordenMateria), nota);
                materiasMap.put(ordenMateria, mejorNota);
            } else {
                materiasMap.put(ordenMateria, nota);
            }
        }
        List<NotaMateriaDTO> notasRefinadas = new ArrayList<>(materiasMap.values());
        if (checkCorrelativas) {
            validarCorrelativas(notasRefinadas);
        }
        return notasRefinadas;
    }

    private void validarCorrelativas(List<NotaMateriaDTO> notasRefinadas) {
        for (NotaMateriaDTO nota : notasRefinadas) {
            List<String> correlativas = nota.getCorrelativas();
            for (String correlativa : correlativas) {
                boolean esValido = false;

                // Verificar si la correlativa es "Aprobadas"
                if ("Aprobadas".equalsIgnoreCase(correlativa)) {
                    esValido = true; // No se bloquea si la correlativa es "Aprobadas"
                } else {
                    // Si no es "Aprobadas", tratar de convertirlo a número y verificar
                    try {
                        int correlativaInt = Integer.parseInt(correlativa);
                        esValido = correlativas(correlativaInt, notasRefinadas, "aprobado");
                    } catch (NumberFormatException e) {
                        // Si no es un número, bloquear
                        esValido = false;
                    }
                }

                if (!esValido) {
                    nota.setNotaFinal("(-)"); // Se bloquea si no cumple correlativas
                    break;
                }
            }
        }
    }


    /**
     * Define el estado final de la nota según su estado actual.
     */
    private String definirNotaFinal(NotaMateriaDTO nota) {
        Set<String> desaprobados = Set.of("Desaprobado", "Libre", "Ausente");

        if (desaprobados.contains(nota.getNotaEstado())) {
            return "Desaprobado";
        }

        // Validar si notaFecha es null antes de llamar a getYear()
        int anioNota = (nota.getNotaFecha() != null) ? nota.getNotaFecha().getYear() : -1;
        int anioActual = LocalDate.now().getYear();

        if ("Cursando".equals(nota.getNotaEstado()) || anioNota == anioActual) {
            return "Cursando";
        }

        switch (nota.getNotaEstado()) {
            case "Regular":
                return "Regular";
            case "Aprobado":
                return nota.getNotaCalificacionNumero() + " (" + nota.getNotaCalificacionLetra() + ")";
            default:
                return "(-)";
        }
    }


    /**
     * Valida las correlativas de las materias en las notas refinadas.
     */
//    private void validarCorrelativas(List<NotaMateriaDTO> notasRefinadas) {
//        for (NotaMateriaDTO nota : notasRefinadas) {
//            List<String> correlativas = nota.getCorrelativas();
//            for (String correlativa : correlativas) {
//                if (!correlativas(Integer.parseInt(correlativa), notasRefinadas, "aprobado")) {
//                    nota.setNotaFinal("(-)"); // Se bloquea si no cumple correlativas
//                    break;
//                }
//            }
//        }
//    }


    public boolean correlativas(int materia_orden, List<NotaMateriaDTO> analitico, String tipo) {
        boolean aux = false;
        // Imprimir la materia_orden y tamaño del linkedlist
        System.out.println("+materiaOrden: " + materia_orden);
        System.out.println("Tamaño del linkedlist: " + analitico.size());

        if (analitico != null && !analitico.isEmpty() && materia_orden >= 0 && materia_orden < analitico.size()) {
            // Asegurarse de que materia_orden no sea 0 para evitar index out of bounds
            if (materia_orden <= 0) {
                System.out.println("Error: materia_orden no puede ser 0 o negativo.");
                return aux;
            }

            // Intentar obtener el objeto NotaMateriaDTO
            NotaMateriaDTO xd = analitico.get(materia_orden - 1);

            // Comprobar si el objeto es nulo
            if (xd != null) {
                System.out.println("Objeto NotaMateriaDTO encontrado: " + xd.toString());

                String cond = xd.getNotaEstado() != null ? xd.getNotaEstado() : "";
                String valorString = xd.getNotaCalificacionNumero() != null ? xd.getNotaCalificacionNumero() : "";

                // Verificar el estado y calificación
                System.out.println("Nota Estado: " + cond);
                System.out.println("Valor de calificación: " + valorString);
                System.out.println("Materia: " + xd.getMateriaNombre() + ", Nota Letra: " + xd.getNotaCalificacionLetra());

                double nota;
                try {
                    nota = Double.parseDouble(valorString);
                    System.out.println("Nota convertida correctamente: " + nota);
                } catch (NumberFormatException e) {
                    // Si no se puede convertir a número, mostrar el error y establecer valor por defecto
                    System.out.println("Error al convertir la calificación a número: " + valorString);
                    nota = 1.0; // Valor por defecto en caso de error
                }

                // Evaluar en función del tipo
                switch (tipo) {
                    case "regular":
                        System.out.println("Evaluando tipo: regular");
                        if (nota >= 4) {
                            System.out.println("Correlativa aceptada, materia_orden: " + materia_orden + ", Nota: " + nota);
                            aux = true;
                        } else {
                            System.out.println("Correlativa rechazada, materia_orden: " + materia_orden + ", Nota: " + nota);
                        }
                        break;
                    case "aprobado":
                        System.out.println("Evaluando tipo: aprobado");
                        if (nota >= 4 && cond.equals("Aprobado")) {
                            aux = true;
                            System.out.println("Correlativa aprobada, materia_orden: " + materia_orden + ", Nota: " + nota);
                        } else {
                            System.out.println("Correlativa desaprobada, materia_orden: " + materia_orden + ", Nota: " + nota + ", Estado: " + cond);
                        }
                        break;
                    default:
                        System.out.println("Tipo no reconocido: " + tipo);
                        break;
                }
            } else {
                System.out.println("El objeto xd no tiene la estructura esperada.");
            }
        } else {
            System.out.println("Los datos de entrada no son válidos.");
            System.out.println("Condiciones: analitico = " + analitico + ", materia_orden = " + materia_orden);
        }

        return aux;
    }


    public boolean buscarClaveAnalitico(List<NotaMateriaDTO> analitico, NotaMateriaDTO materia) {
        return analitico.stream()
                .anyMatch(xd -> Objects.equals(xd.getMateriaNombre(), materia.getMateriaNombre()));
    }


//    public List<NotaMateriaDTO> validadorAnalitico(List<NotaMateriaDTO> analitico, NotaMateriaDTO materia) {
//        List<NotaMateriaDTO> nuevalista = new ArrayList<>();
//        System.out.println(".-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.");
//        for (NotaMateriaDTO xd : analitico) {
//            System.out.println("_________________________________________________________________________");
//           // System.out.println("materias son " + xd.getMateriaNombre() + "|" + materia.getNombre());
//
//            if (xd.getMateriaNombre().equals(materia.getMateriaNombre())) {
//                // Si las materias se repiten
//                String cond1 = xd.getNotaEstado();
//                String cond2 = materia.getNotaEstado();
//                System.out.println("condicion 1: " + cond1 + " | condicion 2: " + cond2);
//
//                if (cond1.equals("Aprobado")) {
//                    // Si la primera materia está aprobada, no hacer nada y conservar su estado
//                    System.out.println("La primera materia está aprobada, se conserva");
//                    nuevalista.add(xd);
//                } else if (cond2.equals("Aprobado")) {
//                    // Si la segunda materia está aprobada, actualizar y conservar su estado
//                    System.out.println("La segunda materia está aprobada, se reemplaza");
//                    nuevalista.add(materia);
//                } else if (cond1.equals("Cursando") && (cond2.equals("Regular") || cond2.equals("Libre") || cond2.equals("Desaprobado") || cond2.equals("Ausente"))) {
//                    // La primera materia está cursando y la segunda está en estado regular o libre, actualizar a cursando
//                    System.out.println("La primera materia está cursando y la segunda está en estado regular o libre, se conserva");
//                    nuevalista.add(xd);
//                } else if (cond1.equals("Regular") && cond2.equals("Regular")) {
//                    // Ambas materias están regulares, actualizar a la última
//                    System.out.println("Ambas materias están regulares, se reemplaza");
//                    nuevalista.add(materia);
//                } else if (cond1.equals("Desaprobado") && cond2.equals("Desaprobado")) {
//                    // Ambas materias están desaprobadas, actualizar a la última
//                    System.out.println("Ambas materias están desaprobadas, se reemplaza");
//                    nuevalista.add(materia);
//                } else if (cond1.equals("Regular") && cond2.equals("Desaprobado")) {
//                    // La primera materia está en estado regular y la segunda está desaprobada, conservar el estado de la primera
//                    System.out.println("La primera materia está regular y la segunda está desaprobada, se conserva");
//                    nuevalista.add(xd);
//                } else if (cond1.equals("Regular") && cond2.equals("Cursando")) {
//                    // La primera materia está en estado regular y la segunda está cursando, conservar el estado de la primera
//                    System.out.println("La primera materia está regular y la segunda está cursando, se conserva");
//                    nuevalista.add(xd);
//                } else if (cond1.equals("Regular") && cond2.equals("Ausente")) {
//                    // La primera materia está en estado regular y la segunda está cursando, conservar el estado de la primera
//                    System.out.println("La primera materia está regular y la segunda está Ausente, se conserva");
//                    nuevalista.add(xd);
//                } else if (cond1.equals("Desaprobado") && cond2.equals("Regular")) {
//                    // La primera materia está desaprobada y la segunda está en estado regular, actualizar a regular
//                    System.out.println("La primera materia está desaprobada y la segunda está regular, se reemplaza");
//                    nuevalista.add(materia);
//                } else if (cond1.equals("Desaprobado") && cond2.equals("Cursando")) {
//                    // La primera materia está desaprobada y la segunda está cursando, conservar el estado de la primera
//                    System.out.println("La primera materia está desaprobada y la segunda está cursando, se reemplaza");
//                    nuevalista.add(materia);
//                } else if (cond1.equals("Libre") && cond2.equals("Cursando")) {
//                    // La primera materia está libre y la segunda está cursando, conservar el estado de la primera
//                    System.out.println("La primera materia está libre y la segunda está cursando, se reemplaza");
//                    nuevalista.add(materia);
//                } else if (cond1.equals("Libre") && cond2.equals("Ausente")) {
//                    // La primera materia está libre y la segunda está cursando, conservar el estado de la primera
//                    System.out.println("La primera materia está libre y la segunda está Ausente, se conserva");
//                    nuevalista.add(xd);
//                } else if (cond1.equals("Ausente") && cond2.equals("Libre")) {
//                    // La primera materia está libre y la segunda está cursando, conservar el estado de la primera
//                    System.out.println("La primera materia está Ausente y la segunda está Libre, se reemplaza");
//                    nuevalista.add(materia);
//                } else if (cond1.equals("Ausente") && cond2.equals("Regular")) {
//                    // La primera materia está libre y la segunda está cursando, conservar el estado de la primera
//                    System.out.println("La primera materia está Ausente y la segunda está Libre, se reemplaza");
//                    nuevalista.add(materia);
//                } else {
//                    // Agregar cualquier otra combinación de estados
//                    System.out.println("Combinación de estados no contemplada, se conserva");
//                    nuevalista.add(xd);
//                }
//            } else {
//                nuevalista.add(xd);
//            }
//        }
//
//        return nuevalista;
//    }


    public NotaMateriaDTO validadorAnalitico(NotaMateriaDTO nota1, NotaMateriaDTO nota2) {
        String cond1 = nota1.getNotaEstado();
        String cond2 = nota2.getNotaEstado();

        // Dar prioridad a la materia aprobada
        if (cond1.equals("Aprobado")) return nota1;
        if (cond2.equals("Aprobado")) return nota2;

        // Si ambas están en estado regular, quedarse con la última
        if (cond1.equals("Regular") && cond2.equals("Regular")) return nota2;

        // Si una es Regular y la otra Desaprobado, quedarse con la Regular
        if (cond1.equals("Regular") && cond2.equals("Desaprobado")) return nota1;
        if (cond1.equals("Desaprobado") && cond2.equals("Regular")) return nota2;

        // Si una es Regular y la otra Cursando, quedarse con la Regular
        if (cond1.equals("Regular") && cond2.equals("Cursando")) return nota1;
        if (cond1.equals("Cursando") && cond2.equals("Regular")) return nota2;

        // Si una es Libre y la otra Cursando, quedarse con la Cursando
        if (cond1.equals("Libre") && cond2.equals("Cursando")) return nota2;
        if (cond1.equals("Cursando") && cond2.equals("Libre")) return nota1;

        // Si una es Ausente y la otra tiene otro estado, quedarse con el otro estado
        if (cond1.equals("Ausente")) return nota2;
        if (cond2.equals("Ausente")) return nota1;

        // En cualquier otro caso, quedarse con la última
        return nota2;
    }


    @Override
    public List<NotaExamenDTO> obtenerNotasPorCondicion(Long cursadaExamenId, boolean examenInscripto, String notaCondicion) {
        return notaRepository.findExamenesByCursadaExamenIdMateriaCarrera(cursadaExamenId, examenInscripto, notaCondicion);
    }

    @Transactional
    @Override
    public void eliminarNota(Long id) {

        Nota nota = notaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nota no encontrada con ID: " + id));

        // Si hay exámenes asociados, no permitir la eliminación
        if (!nota.getExamen().isEmpty()) {
            throw new RuntimeException("No se puede eliminar la Nota porque tiene Exámenes asociados.");
        }

        Long cursadaId = Long.valueOf(nota.getCursada().getId()); // Guardamos el ID de la cursada antes de eliminar la nota

        // Eliminar la nota
        notaRepository.delete(nota);

        // Verificar si hay otras notas con la misma cursada_id
        int countNotas = Math.toIntExact(notaRepository.countByCursadaId(Math.toIntExact(cursadaId)));
        if (countNotas == 0) {
            // Si no hay más notas asociadas, eliminar la cursada
            cursadaService.eliminarCursada(Math.toIntExact(cursadaId));
        }
    }
}


