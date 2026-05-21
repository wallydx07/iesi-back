package com.example.iesiback.services;

import com.example.iesiback.dto.*;
import com.example.iesiback.entities.AsistenciaPersonal;
import com.example.iesiback.repositories.AsistenciaPersonalRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AsistenciaPersonalServiceImpl implements AsistenciaPersonalService {

    @Autowired
    private FeriadosService feriadosService;

    private final AsistenciaPersonalRepository repository;
    private final HorarioService horarioService;

    public AsistenciaPersonalServiceImpl(AsistenciaPersonalRepository repository, HorarioService horarioService) {
        this.repository = repository;
        this.horarioService = horarioService;
    }

    // ==========================================
    //          MÉTODOS CRUD BÁSICOS
    // ==========================================
    @Override
    public List<AsistenciaPersonal> obtenerTodas() {
        return repository.findAll();
    }

    @Override
    public List<AsistenciaPersonal> buscarPorDni(Long dni) {
        return repository.findByDni(dni);
    }

    @Override
    public Optional<AsistenciaPersonal> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    @Override
    public AsistenciaPersonal guardar(AsistenciaPersonal asistencia) {
        return repository.save(asistencia);
    }

    @Override
    public void eliminar(Integer id) {
        repository.deleteById(id);
    }

    // ==========================================
    //     CORE: PROCESAMIENTO DE ASISTENCIA
    // ==========================================
    @Override
    public RespuestaAsistenciaDTO AsistenciaDahua(RegistroAsistenciaDTO asistencia) {
        String dni = asistencia.getDni();
        String fechaHoraStr = asistencia.getFechaHora();
        Long dniLong;

        try {
            dniLong = Long.parseLong(dni);
        } catch (NumberFormatException e) {
            return new RespuestaAsistenciaDTO(false, "❌ DNI inválido.");
        }

        LocalDateTime fechaHoraCompleta;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            fechaHoraCompleta = LocalDateTime.parse(fechaHoraStr, formatter);
        } catch (Exception e) {
            return new RespuestaAsistenciaDTO(false, "❌ Fecha y hora con formato incorrecto.");
        }

        LocalDate hoy = fechaHoraCompleta.toLocalDate();
        LocalTime ahora = fechaHoraCompleta.toLocalTime();

        // 1. Evitar marcas duplicadas en menos de 3 minutos (180 segundos) de cualquier marca anterior
        Optional<AsistenciaPersonal> ultimaMarca = repository.findTopByDniAndFechaOrderByHoraEntradaDesc(dniLong, hoy);
        if (ultimaMarca.isPresent()) {
            LocalTime ultimaHora = ultimaMarca.get().getHoraSalida() != null ?
                    ultimaMarca.get().getHoraSalida() : ultimaMarca.get().getHoraEntrada();

            if (Duration.between(ultimaHora, ahora).getSeconds() < 180) {
                return new RespuestaAsistenciaDTO(false, "⚠️ Registro duplicado muy reciente (menos de 3 minutos).");
            }
        }

        // 2. Buscar si hay una jornada abierta hoy (Entrada registrada pero sin Salida)
        Optional<AsistenciaPersonal> asistenciaAbierta = repository.findFirstByDniAndFechaAndHoraSalidaIsNullOrderByHoraEntradaAsc(dniLong, hoy);

        if (asistenciaAbierta.isPresent()) {
            // Ya hay entrada -> Registramos la SALIDA
            AsistenciaPersonal asistenciaExistente = asistenciaAbierta.get();
            asistenciaExistente.setHoraSalida(ahora);
            // El estado se puede mantener en 0 (Presente) o calcular según requerimientos futuros
            asistenciaExistente.setEstado(0);
            repository.save(asistenciaExistente);
            return new RespuestaAsistenciaDTO(true, "✅ Salida registrada correctamente a las " + ahora);
        } else {
            // No hay jornada abierta -> Registramos la ENTRADA
            AsistenciaPersonal nuevaAsistencia = new AsistenciaPersonal();
            nuevaAsistencia.setDni(dniLong);
            nuevaAsistencia.setFecha(hoy);
            nuevaAsistencia.setHoraEntrada(ahora);
            nuevaAsistencia.setHoraSalida(null);
            nuevaAsistencia.setHorarioId(null); // Ya no depende de un tramo horario fijo
            nuevaAsistencia.setEstado(0); // 0 = Presente por defecto
            nuevaAsistencia.setObservaciones("Entrada biométrica");
            repository.save(nuevaAsistencia);
            return new RespuestaAsistenciaDTO(true, "✅ Entrada registrada correctamente a las " + ahora);
        }
    }

    @Override
    public void actualizarAsistencia(Integer id, AsistenciaPersonal update) {
        AsistenciaPersonal asistencia = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Asistencia no encontrada"));
        asistencia.setFecha(update.getFecha());
        asistencia.setHoraEntrada(update.getHoraEntrada());
        asistencia.setHoraSalida(update.getHoraSalida());
        asistencia.setObservaciones(update.getObservaciones());
        asistencia.setEstado(update.getEstado());
        asistencia.setHorarioId(update.getHorarioId());
        repository.save(asistencia);
    }

    // ==========================================
    //          MÉTODOS DE CONSULTA Y REPORTES
    // ==========================================
    @Override
    public List<DetalleAsistenciaPersonalDTO> obtenerDetallePorFecha(LocalDate fecha) {
        String dia = traducirDia(fecha.getDayOfWeek());
        List<Object[]> resultados = repository.obtenerDetallePorDia(fecha, dia);
        return mapearADetalleDTO(resultados);
    }

    @Override
    public List<AsistenciaPersonal> obtenerPorDniYFecha(Long dni, LocalDate fecha) {
        return repository.findByDniAndFecha(dni, fecha);
    }

    @Override
    public List<DetalleAsistenciaPersonalDTO> obtenerDetallePorYear(Integer year) {
        List<Object[]> resultados = repository.obtenerDetallePorYear(year);
        return mapearADetalleDTO(resultados);
    }

    @Override
    public List<DetalleAsistenciaPersonalDTO> obtenerDetallePorDNI(Long dni) {
        int anioActual = LocalDate.now().getYear();
        List<Object[]> resultados = repository.obtenerDetallePorDniYAnio(dni, anioActual);
        return mapearADetalleDTO(resultados);
    }

    @Override
    public List<ReporteFaltasDTO> cargarAsistenciasDelMesRango(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Object[]> resultados = repository.obtenerReporteFaltasPorRango(fechaInicio, fechaFin);
        return resultados.stream().map(obj -> new ReporteFaltasDTO(
                obj[0] != null ? (String) obj[0] : "",
                obj[1] != null ? (String) obj[1] : "",
                obj[2] != null ? String.valueOf(obj[2]) : "",
                obj[3] != null ? ((Number) obj[3]).intValue() : 0,
                obj[4] != null ? ((Number) obj[4]).intValue() : 0,
                obj[5] != null ? ((Number) obj[5]).intValue() : 0,
                obj[6] != null ? ((Number) obj[6]).intValue() : 0,
                obj[7] != null ? ((Number) obj[7]).intValue() : 0,
                obj[8] != null ? ((Number) obj[8]).intValue() : 0
        )).toList();
    }

    @Override
    public List<AsistenciaDetalleDTO> obtenerAsistenciasPorFecha(LocalDate fecha) {
        List<Object[]> result = repository.buscarAsistenciasPorFechaYDía(fecha);
        List<AsistenciaDetalleDTO> asistencias = new ArrayList<>();

        for (Object[] r : result) {
            String horaEntrada = r[7] != null ? r[7].toString() : null;
            String horaSalida  = r[8] != null ? r[8].toString() : null;
            Integer estado = r[10] != null ? (Integer) r[10] : null;

            AsistenciaDetalleDTO dto = new AsistenciaDetalleDTO(
                    r[0] != null ? ((Integer) r[0]).longValue() : null,
                    r[1] != null ? ((Integer) r[1]).longValue() : null,
                    (String) r[2],
                    (String) r[3],
                    r[4] != null ? String.valueOf(r[4]) : null,
                    (String) r[5],
                    (String) r[6],
                    horaEntrada,
                    horaSalida,
                    (String) r[9],
                    getEstadoTexto(estado)
            );
            asistencias.add(dto);
        }
        return asistencias;
    }

    @Transactional
    @Override
    public int actualizarObservacionesYEstadoPorFecha(String observaciones, String estado, LocalDate fecha) {
        return repository.actualizarPorFecha(observaciones, estado, fecha);
    }

    @Override
    public boolean existeAsistenciaParaHoy(Long horarioId, LocalDate fecha) {
        return repository.existsByHorarioIdAndFecha(horarioId, fecha);
    }

    // ==========================================
    //           MÉTODOS AUXILIARES
    // ==========================================
    private List<DetalleAsistenciaPersonalDTO> mapearADetalleDTO(List<Object[]> resultados) {
        List<DetalleAsistenciaPersonalDTO> lista = new ArrayList<>();
        for (Object[] fila : resultados) {
            DetalleAsistenciaPersonalDTO dto = new DetalleAsistenciaPersonalDTO(
                    fila[0] != null ? ((Number) fila[0]).longValue() : null,
                    fila[1] != null ? ((Number) fila[1]).longValue() : null,
                    (String) fila[2],
                    (String) fila[3],
                    fila[4] != null ? ((Number) fila[4]).longValue() : 0L,
                    (String) fila[5],
                    (String) fila[6],
                    (String) fila[7],
                    (String) fila[8],
                    (String) fila[9],
                    (Integer) fila[10],
                    (fila[11] != null) ? ((java.sql.Date) fila[11]).toLocalDate() : null
            );
            lista.add(dto);
        }
        return lista;
    }

    public String getEstadoTexto(Integer estado) {
        if (estado == null) return "Desconocido";
        return switch (estado) {
            case 0 -> "Presente";
            case 1 -> "Tardanza";
            case 2 -> "Salida Temprana";
            case 3 -> "Falta Justificada";
            case 4 -> "Falta Injustificada";
            case 5 -> "Requiere Informacion";
            case 6 -> "MESA DE EXAMEN";
            case 7 -> "JORNADA INSTITUCIONAL";
            case 8 -> "FERIADO";
            case 9 -> "SUSPENSION DE ACTIVIDADES";
            default -> "Desconocido";
        };
    }

    private String traducirDia(DayOfWeek dia) {
        return switch (dia) {
            case MONDAY -> "LUNES";
            case TUESDAY -> "MARTES";
            case WEDNESDAY -> "MIERCOLES";
            case THURSDAY -> "JUEVES";
            case FRIDAY -> "VIERNES";
            case SATURDAY -> "SABADO";
            case SUNDAY -> "DOMINGO";
        };
    }
}