package com.example.iesiback.services;

import com.example.iesiback.dto.ExamenCursadaDTO;
import com.example.iesiback.dto.NotaExamenDTO;
import com.example.iesiback.dto.PersonaDTO;
import com.example.iesiback.entities.CursadaExamen;
import com.example.iesiback.entities.Turno;
import com.example.iesiback.repositories.CursadaExamenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CursadaExamenServiceImpl implements CursadaExamenService {


    private final TurnoService turnoService;

    private final NotaService notaService;

    private final UserService userService;

    private final PersonaService personaService;

    @Autowired
    private CursadaExamenRepository cursadaExamenRepository;

    @Autowired
    public CursadaExamenServiceImpl(TurnoService turnoService, NotaService notaService, UserService userService, PersonaService personaService) {
        this.turnoService = turnoService;
        this.notaService = notaService;
        this.userService = userService;
        this.personaService = personaService;
    }


    @Override
    public boolean existePorTurnoYMateria(String turnoId, String materiaId) {
        return cursadaExamenRepository.existsByTurno_TurnoIdAndMateriaId(turnoId, materiaId);
    }

    @Override
    public String obtenerFechaPorMateriaYTurno(String materiaId, String turnoId) {
        Optional<LocalDate> fechaOpt = cursadaExamenRepository.findFechaByMateriaIdAndTurnoId(materiaId, turnoId);
        return fechaOpt.map(LocalDate::toString).orElse("No asignado");
    }

    @Override
    public String obtenerHoraPorMateriaYTurno(String materiaId, String turnoId) {
        Optional<String> horaOpt = cursadaExamenRepository.findHoraByMateriaIdAndTurnoId(materiaId, turnoId);
        return horaOpt.orElse("No asignada");
    }

    public List<CursadaExamen> obtenerTodasLasCursadas() {
        return cursadaExamenRepository.findAll();
    }

    @Override
    public List<ExamenCursadaDTO> obtenerCursadasPorTurno(String turnoId) {

        List<Object[]> resultados = cursadaExamenRepository.findByTurno(turnoId);
        List<ExamenCursadaDTO> lista = new ArrayList<>();





        for (Object[] obj : resultados) {

            System.out.println("========================================");
            for (int i = 0; i < obj.length; i++) {
                System.out.println(
                        "obj[" + i + "] = [" + obj[i] + "]" +
                                " tipo=" + (obj[i] == null ? "null" : obj[i].getClass().getName())
                );
            }
            System.out.println("========================================");








            PersonaDTO titular = null;
            if (obj[6] != null) {
                Long dni = Long.valueOf(obj[6].toString());
                titular = personaService.findPersonaDTOById(dni);
            }

            PersonaDTO vocal1 = null;
            if (obj[11] != null) {
                Long dni = Long.valueOf(obj[11].toString());
                vocal1 = personaService.findPersonaDTOById(dni);
            }

            PersonaDTO vocal2 = null;
            if (obj[12] != null) {
                Long dni = Long.valueOf(obj[12].toString());
                vocal2 = personaService.findPersonaDTOById(dni);
            }

            ExamenCursadaDTO dto = new ExamenCursadaDTO(
                    Objects.toString(obj[0], null),
                    obj[1] != null ? LocalDate.parse(obj[1].toString()) : null,
                    Objects.toString(obj[2], null),
                    Objects.toString(obj[3], null),
                    Objects.toString(obj[4], null),
                    obj[5] != null && Boolean.parseBoolean(obj[5].toString()),
                    titular,
                    Objects.toString(obj[7], null),
                    Objects.toString(obj[8], null),
                    Objects.toString(obj[9], null),
                    Objects.toString(obj[10], null),
                    vocal1,
                    vocal2
            );

            lista.add(dto);
        }


        return lista;
    }


//    @Override
//    public List<ExamenCursadaDTO> obtenerCursadasPorTurnoFechaDesc(String turnoId) {
//        List<Object[]> resultados = cursadaExamenRepository.findByTurnoFechaDesc(turnoId);
//        return resultados.stream().map(obj -> new ExamenCursadaDTO(
//                Objects.toString(obj[0], null),                               // cursadaExamenId
//                obj[1] != null ? LocalDate.parse(obj[1].toString()) : null,   // fecha
//                Objects.toString(obj[2], null),                               // hora
//                Objects.toString(obj[3], null),                               // libro
//                Objects.toString(obj[4], null),                               // folio
//                obj[5] != null && Boolean.parseBoolean(obj[5].toString()),    // firma (booleano)
//                Objects.toString(obj[6], null),                               // docente_dni
//                Objects.toString(obj[7], null),                               // materiaId
//                Objects.toString(obj[8], null),                               // materiaNombre
//                Objects.toString(obj[9], null),                              // carreraNombre
//                Objects.toString(obj[10], null)                                // carreraNombre
//        )).collect(Collectors.toList());
//    }






//
//
//    @Override
//    public List<ExamenCursadaDTO> obtenerCursadasPorTurnoFechaDesc(String turnoId) {
//
//        List<Object[]> resultados = cursadaExamenRepository.findByTurnoFechaDesc(turnoId);
//
//        List<ExamenCursadaDTO> lista = new ArrayList<ExamenCursadaDTO>();
//
//        // 1️⃣ Primero cargo el vector
//        for (Object[] obj : resultados) {
//
//            PersonaDTO titular = null;
//
//            if (obj[6] != null) {
//                Long dni = Long.valueOf(obj[6].toString());
//                titular = personaService.findPersonaDTOById(dni);
//            }
//
//
//            PersonaDTO vocal1 = null;
//            if (obj[11] != null) {
//                Long dni = Long.valueOf(obj[11].toString());
//                vocal1 = personaService.findPersonaDTOById(dni);
//            }
//
//            PersonaDTO vocal2 = null;
//            if (obj[12] != null) {
//                Long dni = Long.valueOf(obj[12].toString());
//                vocal2 = personaService.findPersonaDTOById(dni);
//            }
//
//            ExamenCursadaDTO dto = new ExamenCursadaDTO(
//                    Objects.toString(obj[0], null),
//                    obj[1] != null ? LocalDate.parse(obj[1].toString()) : null,
//                    Objects.toString(obj[2], null),
//                    Objects.toString(obj[3], null),
//                    Objects.toString(obj[4], null),
//                    obj[5] != null && Boolean.parseBoolean(obj[5].toString()),
//                    titular,
//                    Objects.toString(obj[7], null),
//                    Objects.toString(obj[8], null),
//                    Objects.toString(obj[9], null),
//                    Objects.toString(obj[10], null),
//                    vocal1,
//                    vocal2
//            );
//
//            lista.add(dto);
//        }
//
//
//        // 2️⃣ Ahora recorro los objetos ya creados y los modifico
//        for (ExamenCursadaDTO dto : lista) {
//            Long cursadaExamenId= Long.valueOf(dto.getCursadaExamenId());
//             List<NotaExamenDTO> notaExamenDTO=notaService.findExamenesByCursadaExamenIdMateriaCarrera(cursadaExamenId,true);
//             dto.setNotaExamenDTO(notaExamenDTO);
//        }
//
//        return lista;
//    }


    @Override
    public List<ExamenCursadaDTO> obtenerCursadasPorTurnoFechaDesc(String turnoId) {

        List<Object[]> resultados = cursadaExamenRepository.findByTurnoFechaDesc(turnoId);

        List<ExamenCursadaDTO> lista = new ArrayList<>();

        // 🔐 Obtener usuario autenticado
        String userRol = userService.getAuthenticatedUser().get().getRoles().get(0).getRoleNombre();
        Long userId = Long.valueOf(userService.getAuthenticatedUser().get().getUsername());

        for (Object[] obj : resultados) {

            Long titularId = obj[6] != null ? Long.valueOf(obj[6].toString()) : null;
            Long vocal1Id  = obj[11] != null ? Long.valueOf(obj[11].toString()) : null;
            Long vocal2Id  = obj[12] != null ? Long.valueOf(obj[12].toString()) : null;

            // 🔐 Si es docente, validar que participe en la mesa
            if ("ROLE_DOCENTE".equalsIgnoreCase(userRol)) {

                boolean esTitular = titularId != null && titularId.equals(userId);
                boolean esVocal1  = vocal1Id != null && vocal1Id.equals(userId);
                boolean esVocal2  = vocal2Id != null && vocal2Id.equals(userId);

                if (!(esTitular || esVocal1 || esVocal2)) {
                    continue; // ⛔ saltea esta mesa
                }
            }

            // 🔹 Construcción de DTO
            PersonaDTO titular = titularId != null ? personaService.findPersonaDTOById(titularId) : null;
            PersonaDTO vocal1  = vocal1Id  != null ? personaService.findPersonaDTOById(vocal1Id)  : null;
            PersonaDTO vocal2  = vocal2Id  != null ? personaService.findPersonaDTOById(vocal2Id)  : null;

            ExamenCursadaDTO dto = new ExamenCursadaDTO(
                    Objects.toString(obj[0], null),
                    obj[1] != null ? LocalDate.parse(obj[1].toString()) : null,
                    Objects.toString(obj[2], null),
                    Objects.toString(obj[3], null),
                    Objects.toString(obj[4], null),
                    obj[5] != null && Boolean.parseBoolean(obj[5].toString()),
                    titular,
                    Objects.toString(obj[7], null),
                    Objects.toString(obj[8], null),
                    Objects.toString(obj[9], null),
                    Objects.toString(obj[10], null),
                    vocal1,
                    vocal2
            );

            lista.add(dto);
        }

        // 🔹 Cargar notas
        for (ExamenCursadaDTO dto : lista) {
            Long cursadaExamenId = Long.valueOf(dto.getCursadaExamenId());
            List<NotaExamenDTO> notaExamenDTO =
                    notaService.findExamenesByCursadaExamenIdMateriaCarrera(cursadaExamenId, true);

            dto.setNotaExamenDTO(notaExamenDTO);
        }

        return lista;
    }

    @Override
    public Optional<CursadaExamen> obtenerPorId(Integer id) {
        return cursadaExamenRepository.findById(id);
    }

    @Override
    public CursadaExamen crearCursadaExamen(
            String turnoId, String materiaId, LocalDate fecha, String hora, Long docenteDni,Long vocal1Dni, Long vocal2Dni) {
        Turno turno = this.turnoService.obtenerTurnoPorId(turnoId);
        Optional<CursadaExamen> existente = cursadaExamenRepository.findByTurnoAndMateriaId(turno, materiaId);

        if (existente.isPresent()) {
            throw new RuntimeException("Ya existe un CursadaExamen para el turno " + turnoId + " y materia " + materiaId);
        }


        CursadaExamen cursadaExamen = new CursadaExamen();
        cursadaExamen.setTurno(turno);
        cursadaExamen.setMateriaId(materiaId);
        cursadaExamen.setFecha(fecha);
        cursadaExamen.setHora(hora);
        cursadaExamen.setDocenteDni(docenteDni);
        cursadaExamen.setVocal1Dni(String.valueOf(vocal1Dni));
        cursadaExamen.setVocal2Dni(String.valueOf(vocal2Dni));

        return cursadaExamenRepository.save(cursadaExamen);
    }


    @Override
    public CursadaExamen actualizar(Long id, CursadaExamen dto) {

        CursadaExamen e = cursadaExamenRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("No encontrado id: " + id));

        // Actualizar campos simples
        e.setVocal1Dni(dto.getVocal1Dni());
        e.setVocal2Dni(dto.getVocal2Dni());
        e.setLibro(dto.getLibro());
        e.setFolio(dto.getFolio());
        e.setFirma(dto.getFirma());
        e.setDocenteDni(dto.getDocenteDni());
        e.setMateriaId(dto.getMateriaId());

        // Fecha
        if (dto.getFecha() != null) {
            e.setFecha(LocalDate.parse(dto.getFecha().toString()));
        }
        if (dto.getHora() != null) {
            e.setHora(dto.getHora());
        }
        if (dto.getTurno() != null && dto.getTurno().getTurnoId() != null) {
            Turno turno = turnoService.findById(dto.getTurno().getTurnoId())
                    .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
            e.setTurno(turno);
        }


//        notaService
//

        return cursadaExamenRepository.save(e);
    }




    @Override
    public Optional<CursadaExamen> findById(Integer cursadaExamenId) {
        return cursadaExamenRepository.findById(cursadaExamenId);
    }

    @Override
    public Optional<CursadaExamen> findByTurnoAndMateriaId(Turno t, String materiaId) {
        Optional<CursadaExamen> resultados = cursadaExamenRepository.findByTurnoAndMateriaId(t,materiaId);
        return resultados;
    }

    @Override
    public Optional<CursadaExamen> findByTurnoIdAndMateriaId(
            String turnoId,
            String materiaId) {

        return cursadaExamenRepository
                .findByTurnoIdAndMateriaId(turnoId, materiaId);
    }



    @Transactional
    @Override
    public CursadaExamen obtenerOCrearCursadaExamen(
            String turnoId,
            String materiaId) {

        return cursadaExamenRepository
                .findByTurnoIdAndMateriaId(turnoId, materiaId)
                .orElseGet(() -> {

                    Turno turno = turnoService.obtenerTurnoPorId(turnoId);
                    CursadaExamen nueva = new CursadaExamen();
                    nueva.setTurno(turno);
                    nueva.setMateriaId(materiaId);
                    nueva.setFecha(turno.getTurnoLimite());
                    nueva.setFirma(false);

                    return cursadaExamenRepository.save(nueva);
                });
    }


}