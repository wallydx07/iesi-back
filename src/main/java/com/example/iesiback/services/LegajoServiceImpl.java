package com.example.iesiback.services;

import com.example.iesiback.entities.*;
import com.example.iesiback.repositories.LegajoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LegajoServiceImpl implements LegajoService {

    public static final Logger log = LoggerFactory.getLogger(LegajoServiceImpl.class);
    private final ObjectMapper objectMapper;
    private final LegajoRepository legajoRepository;
    private final PersonaService personaService;
    private final UserService userService;
    private final CarreraService carreraService;
    private final InscripcionService inscripcionService;

    public LegajoServiceImpl(ObjectMapper objectMapper, LegajoRepository legajoRepository , PersonaService personaService, UserService userService, CarreraService carreraService, InscripcionService inscripcionService) {
        this.objectMapper = objectMapper;
        this.legajoRepository = legajoRepository;
        this.personaService = personaService;
        this.userService = userService;
        this.carreraService = carreraService;
        this.inscripcionService = inscripcionService;
    }

    @Override
    public Legajo findLegajoById(String id) {
        return legajoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Legajo no encontrado con ID: " + id));
    }

    @Override
    public List<Legajo> obtenerLegajos() {
        return legajoRepository.findAll();
    }

    @Override
    public Optional<Legajo> findById(String id) {
        return legajoRepository.findById(id);
    }

    @Override
    public Legajo updateLegajo(Legajo legajo) {
        if (legajoRepository.existsById(legajo.getLegajoId())) {
            return legajoRepository.save(legajo);  // Actualiza el legajo
        } else {
            throw new EntityNotFoundException("Legajo no encontrado");
        }
    }

    @Override
    public String generaLegajo(String prefijo) {
        System.out.println("---------------------------------------------"+prefijo);
        String maxLegajo = legajoRepository.findMaxLegajoId(prefijo);
        String numeroStr = maxLegajo.replaceAll("\\D+", ""); // Solo deja los dígitos
        int numero = Integer.parseInt(numeroStr) + 1; // Incrementa el número
        return prefijo + numero;
    }

    @Transactional
    public void actualizarAlumnoDNI(Persona personaViejo, Persona nuevoPersona) {
        List<Legajo> legajos = legajoRepository.findByLegajoPersonaDni(personaViejo);
        for (Legajo l : legajos) {
            l.setLegajoPersonaDni(nuevoPersona);
        }
        legajoRepository.saveAll(legajos);
    }

    @Override
    public Legajo findByPersonaAndCarreraId(Long personaDni, String carreraId) {
        return legajoRepository
                .findByLegajoPersonaDni_PersonaDniAndInscripcionCarrera_Carrera_CarreraId(
                        personaDni,
                        carreraId
                )
                .orElseThrow(() ->
                        new RuntimeException("Legajo no encontrado")
                );
    }
@Transactional
@Override
public Legajo findOrCreateLegajo(Persona persona, String carreraId) {

        log.info("🔎 Buscando legajo (dni={}, carrera={})", persona.getPersonaDni(), carreraId);

        return legajoRepository
                .findByLegajoPersonaDni_PersonaDniAndInscripcionCarrera_Carrera_CarreraId(
                        persona.getPersonaDni(), carreraId
                )
                .orElseGet(() -> {
                    log.info("🆕 Legajo no encontrado → iniciando creación");
                    Persona alumno = findOrCreatePersona(persona);
                    Legajo legajo = new Legajo();
                    return crearYGuardarLegajo(alumno.getPersonaDni(), carreraId, legajo);
                });
    }

    private Persona findOrCreatePersona(Persona persona) {

        return personaService.findById(String.valueOf(persona.getPersonaDni()))
                .map(p -> {
                    log.info("👤 Persona encontrada (dni={})", persona.getPersonaDni());
                    return p;
                })
                .orElseGet(() -> {
                    log.warn("⚠️ Persona no encontrada → creando nueva (dni={})", persona.getPersonaDni());
                    return crearPersonaBasica(persona);
                });
    }


    private Persona crearPersonaBasica(Persona persona) {
        return personaService.save(persona);
    }

    @Transactional
    protected Legajo crearYGuardarLegajo(Long personaDni,
                                         String carreraId,
                                         Legajo legajo) {
        log.info("🛠 Creando legajo nuevo → personaDni={}, carreraId={}",
                personaDni, carreraId);
        // 👤 Persona
        Persona persona = personaService.findById(String.valueOf(personaDni))
                .orElseThrow(() ->
                        new RuntimeException("Persona no encontrada: " + personaDni)
                );
        // 🎓 Carrera
        Carrera carrera = carreraService.findCarreraById(carreraId);
        // 🧾 Completar legajo
        legajo.setLegajoPersonaDni(persona);
        legajo.setLegajoFecha(
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        );
        legajo.setUsuario(
                userService.getAuthenticatedUser()
                        .orElseThrow(() -> new RuntimeException("Usuario no autenticado"))
                        .getUserApellido()
        );
        // 🔢 Generar legajoId
        String prefijo = carrera.getCarreraId().split("-")[0];
        log.debug("🔢 Prefijo generado: {}", prefijo);
        String nuevoLegajoId = generaLegajo(prefijo);
        log.info("🆔 LegajoId generado: {}", nuevoLegajoId);
        legajo.setLegajoId(nuevoLegajoId);
        // 💾 Persistir
        Legajo guardado = legajoRepository.save(legajo);
        log.info("✅ Legajo creado y guardado → legajoId={}", guardado.getLegajoId());
        Inscripcion inscripcion=new Inscripcion();
        inscripcion.setLegajo(legajo);
        inscripcion.setCarrera(carrera);


        inscripcionService.crearInscripcion(inscripcion);
//        cursadaService.agregarMateriasACursadaPorCarrera(carrera.getCarreraId(),legajo);

        return guardado;
    }

    @Override
    @Transactional
    public Legajo crearLegajoDesdeRequest(Map<String, Object> request) {
        log.info("➡️ Iniciando crearLegajoDesdeRequest");
        log.debug("📥 Request recibido: {}", request);
        Long alumnoDni = Long.valueOf(request.get("alumnoDni").toString());
        log.info("👤 Alumno DNI: {}", alumnoDni);
        Carrera carrera = objectMapper.convertValue(
                request.get("carrera"), Carrera.class
        );
        log.info("🎓 Carrera recibida: id={}", carrera.getCarreraId());
        Legajo legajoRequest = objectMapper.convertValue(
                request.get("legajo"), Legajo.class
        );
        log.debug("📁 Legajo recibido desde Angular: {}", legajoRequest);
        Legajo legajo = crearYGuardarLegajo(alumnoDni, carrera.getCarreraId(), legajoRequest);
        log.info("✅ Legajo creado/obtenido → legajoId={}", legajo.getLegajoId());
        return legajo;
    }

    @Override
    public List<Legajo> findLegajosByDNI(String dni) {
        var persona = personaService.findAlumnoById(dni);

        if (persona == null) {
            return List.of();
        }

        return legajoRepository.findByLegajoPersonaDni(persona);
    }


}
