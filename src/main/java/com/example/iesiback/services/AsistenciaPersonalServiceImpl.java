package com.example.iesiback.services;

import com.example.iesiback.dto.HorarioDTO;
import com.example.iesiback.entities.AsistenciaPersonal;
import com.example.iesiback.repositories.AsistenciaPersonalRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
@Service
public class AsistenciaPersonalServiceImpl implements AsistenciaPersonalService {

    private final AsistenciaPersonalRepository repository;
    private final HorarioService horarioService;

    public AsistenciaPersonalServiceImpl(AsistenciaPersonalRepository repository, HorarioService horarioService) {
        this.repository = repository;
        this.horarioService = horarioService;
    }

    @Override
    public List<AsistenciaPersonal> obtenerTodas() {
        return repository.findAll();
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

    @Override
    public boolean verificarSiYaEstaRegistrado(LocalDate fecha, Long dni, Integer horarioId) {
        return repository.existsByFechaAndDniAndHorarioId(fecha, dni, horarioId);
    }
    @Override
    public void registrarEntrada(LocalDate fecha, Long dni, Integer horarioId) {
        AsistenciaPersonal asistencia = new AsistenciaPersonal();
        asistencia.setFecha(fecha);
        asistencia.setDni(dni);
        asistencia.setHorarioId(horarioId);
        asistencia.setHoraEntrada(LocalTime.now());
        guardar(asistencia);
    }
    @Override
    public boolean marcarHoraSalida(LocalDate fecha, Long dni, Integer horarioId) {
        Optional<AsistenciaPersonal> existente = repository.findByFechaAndDniAndHorarioId(fecha, dni, horarioId);
        if (existente.isPresent()) {
            AsistenciaPersonal asistencia = existente.get();
            asistencia.setHoraSalida(LocalTime.now());
            guardar(asistencia);
            return true;
        }
        return false;
    }


    @Override
    public boolean AsistenciaDahua(String dni) {
        System.out.println("\n🔍 Iniciando verificación de asistencia para DNI: " + dni);

        List<HorarioDTO> horarios = horarioService.obtenerPorDni(dni);
        if (horarios == null || horarios.isEmpty()) {
            System.out.println("⚠ No se encontraron horarios para el DNI " + dni);
            return false;
        }

        LocalDate hoy = LocalDate.now();

        // LocalTime ahora = LocalTime.now();
        LocalTime ahora = LocalTime.of(12, 0); // 20:30 hs

        String diaActual = traducirDia(hoy.getDayOfWeek());

        System.out.println("📅 Día actual: " + diaActual + " | ⏰ Hora actual: " + ahora);
        System.out.println("📋 Horarios totales del día: " + horarios.size());

        // Filtrar solo horarios del día actual y ordenarlos por hora
        List<HorarioDTO> horariosHoy = horarios.stream()
                .filter(h -> h.getDia().equalsIgnoreCase(diaActual))
                .sorted(Comparator.comparing(h -> LocalTime.parse(h.getEntrada())))
                .toList();

        HorarioDTO horarioInicial = null;
        HorarioDTO horarioActual = null;

        // Buscar el horario actual según la hora
        for (HorarioDTO h : horariosHoy) {
            LocalTime entrada = LocalTime.parse(h.getEntrada());
            LocalTime salida = LocalTime.parse(h.getSalida());

            LocalTime rangoInicio = entrada.minusMinutes(15);
            LocalTime rangoFin = salida.plusMinutes(15);

            if (ahora.isAfter(rangoInicio) && ahora.isBefore(rangoFin)) {
                horarioActual = h;
                break;
            }
        }

        if (horarioActual == null) {
            System.out.println("⛔ No se encontró ningún horario activo ahora para este DNI.");
            return false;
        }

        Integer horarioId = horarioActual.getId();
        Long dniLong = Long.parseLong(dni);

        System.out.println("✅ Horario actual detectado: ID " + horarioId + " (" +
                horarioActual.getEntrada() + " - " + horarioActual.getSalida() + ")");

        boolean yaRegistrado = verificarSiYaEstaRegistrado(hoy, dniLong, horarioId);
        System.out.println("🔍 Ya registrado en este horario: " + yaRegistrado);

        if (!yaRegistrado) {
            registrarEntrada(hoy, dniLong, horarioId);
            System.out.println("✅ Entrada registrada manualmente en horario ID: " + horarioId);
            return true;
        } else {
            boolean salidaRegistrada = marcarHoraSalida(hoy, dniLong, horarioId);
            if (salidaRegistrada) {
                System.out.println("✅ Salida registrada manualmente en horario ID: " + horarioId);

                // Buscar el primer horario con entrada hoy
                for (HorarioDTO h : horariosHoy) {
                    if (verificarSiYaEstaRegistrado(hoy, dniLong, h.getId())) {
                        horarioInicial = h;
                        break;
                    }
                }

                if (horarioInicial != null && !horarioInicial.getId().equals(horarioActual.getId())) {
                    System.out.println("📦 Detectado tramo desde horario ID " + horarioInicial.getId() +
                            " hasta horario ID " + horarioActual.getId());

                    boolean enZona = false;
                    for (HorarioDTO h : horariosHoy) {
                        if (h.getId().equals(horarioInicial.getId())) {
                            enZona = true;
                            continue;
                        }

                        if (h.getId().equals(horarioActual.getId())) break;

                        if (enZona && !verificarSiYaEstaRegistrado(hoy, dniLong, h.getId())) {
                            registrarEntrada(hoy, dniLong, h.getId());
                            marcarHoraSalida(hoy, dniLong, h.getId());
                            System.out.println("⚙️ Horario intermedio ID " + h.getId() +
                                    " marcado automáticamente como asistido.");
                        } else if (enZona) {
                            System.out.println("🔁 Horario intermedio ID " + h.getId() + " ya registrado. Se omite.");
                        }
                    }
                }
                return true;
            } else {
                System.out.println("❌ No se pudo registrar la salida (posiblemente ya registrada).");
                return false;
            }
        }
    }




    private String traducirDia(DayOfWeek dia) {
        switch (dia) {
            case MONDAY: return "lunes";
            case TUESDAY: return "martes";
            case WEDNESDAY: return "miércoles";
            case THURSDAY: return "jueves";
            case FRIDAY: return "viernes";
            case SATURDAY: return "sábado";
            case SUNDAY: return "domingo";
            default: return "";
        }
    }


}