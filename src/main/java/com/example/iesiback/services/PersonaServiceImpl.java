package com.example.iesiback.services;

import com.example.iesiback.dto.NotaMateriaDTO;
import com.example.iesiback.dto.PersonaDTO;
import com.example.iesiback.dto.PromedioEgresadoDTO;
import com.example.iesiback.entities.Carrera;
import com.example.iesiback.entities.Persona;
import com.example.iesiback.repositories.CarreraRepository;
import com.example.iesiback.repositories.PersonaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository personaRepository;
    private final EntityManager entityManager;
    public PersonaServiceImpl(
            PersonaRepository personaRepository
            , EntityManager entityManager
    ) {
        this.personaRepository = personaRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Persona createAlumno(Persona persona) {
        return personaRepository.save(persona);
    }

    @Override
    public Persona findAlumnoById(String id) {
        return personaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con ID: " + id));
    }

    @Override
    public Persona obtenerAlumnoPorLegajoId(String legajoId) {
        return personaRepository.findAlumnoByLegajoId(legajoId);
    }

    @Override
    public List<Persona> obtenerAlumnos() {
        return personaRepository.findAll();
    }

    @Override
    public Optional<Persona> findById(String id) {
        return personaRepository.findById(id);
    }

    @Override
    public Persona save(Persona persona) {
        return personaRepository.save(persona);
    }

    @Override
    public boolean delete(String id) {
        return personaRepository.findById(id).map(alumno -> {
            personaRepository.delete(alumno);
            return true;
        }).orElse(false);
    }

    @Override
    public boolean existsById(String id) {
        personaRepository.existsById(id);
        return false;
    }

    @Override
    public List<String> buscarPorDniApellidoNombre(String busqueda) {
        if (busqueda == null || busqueda.trim().isEmpty()) {
            return personaRepository.buscarPorDniApellidoNombre(null);
        }
        String[] palabras = busqueda.trim().split("\\s+");
        String busquedaParam = "%" + String.join("%", palabras) + "%"; // Ej: %juan%perez%
        return personaRepository.buscarPorDniApellidoNombre(busquedaParam.toLowerCase());
    }

    @Override
    public List<String> buscarPersonalPorDniApellidoNombre(String busqueda) {
        if (busqueda == null || busqueda.trim().isEmpty()) {
            return personaRepository.buscarPersonalPorDniApellidoNombre(null);
        }
        String[] palabras = busqueda.trim().split("\\s+");
        String busquedaParam = "%" + String.join("%", palabras) + "%"; // Ej: %juan%perez%
        return personaRepository.buscarPersonalPorDniApellidoNombre(busquedaParam.toLowerCase());
    }

    @Override
    public List<Persona> buscarPorDni(String dni) {
        return personaRepository.buscarPorDni(dni);
    }

    @Override
    public List<String> buscarPorApellidoYCarrera(String busqueda, String carreraNombre) {
        String busquedaParam = null;
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            String[] palabras = busqueda.trim().split("\\s+");
            busquedaParam = "%" + String.join("%", palabras) + "%";
        }
        return personaRepository.buscarPorApellidoYCarrera(
                busquedaParam != null ? busquedaParam.toLowerCase() : null,
                carreraNombre != null && !carreraNombre.trim().isEmpty() ? carreraNombre.toLowerCase() : null
        );
    }




//    @Override
//    public List<PromedioEgresadoDTO> obtenerEgresados(Integer year) {
//        List<String> legajos = personaRepository
//                .obtenerLegajosPorCarreraYearNative(year);
//        List<PromedioEgresadoDTO> resultado = new ArrayList<>();
//        for (String legajo : legajos) {
//            if (!verificarMateriasAprobadas(legajo)) {
//                continue;
//            }
//            Double promedio = calcularPromedio(legajo);
//            if (promedio != null) {
//
//                Persona persona = personaRepository.findAlumnoByLegajoId(legajo);
//                Carrera carrera = carreraService.obtenerCarreraPorLegajoId(legajo);
//                resultado.add(new PromedioEgresadoDTO(legajo, promedio, persona.getPersonaNombre(), persona.getPersonaApellido(), persona.getPersonaDni().toString(), carrera.getCarreraNombre()));
//            }
//        }
//        return resultado;
//    }
//
//    private boolean verificarMateriasAprobadas(String legajo) {
//
//        List<NotaMateriaDTO> listaMaterias =
//                notaService.obtenerTodasNotasPorLegajoAnalitico(legajo);
//        // 1️⃣ Si no tiene ninguna nota → afuera
//        if (listaMaterias == null || listaMaterias.isEmpty()) {
//            return false;
//        }
//        // 2️⃣ Si existe alguna materia en CURSADA → afuera
//        boolean tieneCursada = listaMaterias.stream()
//                .anyMatch(n ->
//                        n.getNotaEstado() != null &&
//                                n.getNotaEstado().equalsIgnoreCase("Cursada")
//                );
//        if (tieneCursada) {
//            return false;
//        }
//        // 3️⃣ Agrupar por materiaId
//        Map<String, List<NotaMateriaDTO>> materiasAgrupadas =
//                listaMaterias.stream()
//                        .collect(Collectors.groupingBy(NotaMateriaDTO::getMateriaId));
//
//        // 4️⃣ Verificar que TODAS las materias tengan al menos un aprobado
//        for (Map.Entry<String, List<NotaMateriaDTO>> entry : materiasAgrupadas.entrySet()) {
//            List<NotaMateriaDTO> intentosMateria = entry.getValue();
//            boolean aprobada = intentosMateria.stream()
//                    .anyMatch(n ->
//                            n.getNotaEstado() != null &&
//                                    n.getNotaEstado().equalsIgnoreCase("Aprobado")
//                    );
//            // ❌ si una materia nunca fue aprobada
//            if (!aprobada) {
//                return false;
//            }
//        }
//        // ✅ Cumple todas las condiciones
//        return true;
//    }
//
//    private Double calcularPromedio(String legajo) {
//        List<NotaMateriaDTO> listaMaterias =
//                notaService.obtenerTodasNotasPorLegajoAnalitico(legajo);
//        // Agrupar por materia
//        Map<String, List<NotaMateriaDTO>> materiasAgrupadas =
//                listaMaterias.stream()
//                        .collect(Collectors.groupingBy(NotaMateriaDTO::getMateriaId));
//        List<Double> notasFinales = new ArrayList<>();
//        for (List<NotaMateriaDTO> intentosMateria : materiasAgrupadas.values()) {
//            // Tomar la mejor nota aprobada
//            Optional<Double> mejorNota = intentosMateria.stream()
//                    .filter(n ->
//                            n.getNotaEstado() != null &&
//                                    n.getNotaEstado().equalsIgnoreCase("Aprobado") &&
//                                    n.getNotaCalificacionNumero() != null
//                    )
//                    .map(NotaMateriaDTO::getNotaCalificacionNumero)
//                    .max(Double::compareTo);
//            if (mejorNota.isPresent()) {
//                notasFinales.add(mejorNota.get());
//            }
//        }
//        if (notasFinales.isEmpty()) {
//            return null;
//        }
//        // Promedio simple
//        return notasFinales.stream()
//                .mapToDouble(Double::doubleValue)
//                .average()
//                .orElse(0.0);
//    }
//

    @Override
    public PersonaDTO findPersonaDTOById(Long id) {
        PersonaDTO persona = personaRepository.findPersonaDTObyDNI(id).orElse(null);
        System.out.println("Persona encontrada para id " + id + ": " + persona);
        return persona;
    }


    @Transactional
    @Override
    public void cambiarDni(Long dniActual, Long dniNuevo) {

        int filas = personaRepository.actualizarDni(dniActual, dniNuevo);

        if (filas == 0) {
            throw new RuntimeException("No se encontró la persona con DNI " + dniActual);
        }

        // Limpia el contexto por si Hibernate tenía la entidad cargada
        entityManager.clear();
    }


//@Override
//public List<AlumnoExamenDTO> AlumnoExamenDTO(String apellido, String carreraNombre) {
//        return alumnoRepository.buscarAlumnos(apellido, carreraNombre);
//    }
}
