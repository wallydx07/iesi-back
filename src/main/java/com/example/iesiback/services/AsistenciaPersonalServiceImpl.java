package com.example.iesiback.services;

import com.example.iesiback.dto.*;
import com.example.iesiback.entities.AsistenciaPersonal;
import com.example.iesiback.entities.PersonalHorario;
import com.example.iesiback.repositories.AsistenciaPersonalRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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


//    @Override
//    public boolean AsistenciaDahua(String dni) {
//        System.out.println("\n🔍 Iniciando verificación de asistencia para DNI: " + dni);
//        Long dniLong;
//        try {
//            dniLong = Long.parseLong(dni);
//        } catch (NumberFormatException e) {
//            System.out.println("❌ DNI inválido: " + dni);
//            return false;
//        }
//        LocalDate hoy = LocalDate.now();
//        LocalTime ahora = LocalTime.now(); // Para pruebas puede usarse fijo
//        List<HorarioDTO> horariosHoy = obtenerHorariosDelDia(dni, hoy);



    /// ============================================================000
    /// ASISTENCIA!!!!!!

    @Override
    public RespuestaAsistenciaDTO AsistenciaDahua(RegistroAsistenciaDTO asistencia) {
        String dni = asistencia.getDni();
        String fechaHoraStr = asistencia.getFechaHora();
        String dispositivo = asistencia.getDispositivo();

        System.out.println("\n🔍 Iniciando verificación de asistencia:");
        System.out.println("📄 DNI: " + dni);
        System.out.println("🕓 Fecha y hora recibida: " + fechaHoraStr);
        System.out.println("🖥 Dispositivo: " + dispositivo);

        Long dniLong;
        try {
            dniLong = Long.parseLong(dni);
        } catch (NumberFormatException e) {
            System.out.println("❌ DNI inválido: " + dni);
            return new RespuestaAsistenciaDTO(false, "❌ DNI inválido.");
        }

        LocalDateTime fechaHoraCompleta;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            fechaHoraCompleta = LocalDateTime.parse(fechaHoraStr, formatter);
        } catch (Exception e) {
            System.out.println("❌ Error al parsear fechaHora: " + fechaHoraStr);
            return new RespuestaAsistenciaDTO(false, "❌ Fecha y hora con formato incorrecto.");
        }

        LocalDate hoy = fechaHoraCompleta.toLocalDate();
        LocalTime ahora = fechaHoraCompleta.toLocalTime();

        // 🧱 Validar duplicado en últimos 60 segundos
//        Optional<AsistenciaPersonal> ultima = repository.findUltimoRegistroPorDni(dniLong);

        Optional<AsistenciaPersonal> ultima = repository.findTopByDniAndFechaOrderByHoraEntradaDesc(dniLong,hoy);
        if (ultima.isPresent()) {
            Duration diff = Duration.between(ultima.get().getHoraEntrada(), fechaHoraCompleta.toLocalTime());
            if (diff.getSeconds() < 60) {
                System.out.println("⚠️ Registro ignorado: diferencia menor a 60 segundos con el anterior.");
                return new RespuestaAsistenciaDTO(false, "⚠️ Registro duplicado muy reciente (menos de 60 segundos).");
            }
        }

        List<HorarioDTO> horariosHoy = obtenerHorariosDelDia(dni, hoy);

        if (horariosHoy.isEmpty()) {
            System.out.println("⚠ No hay horarios para hoy.");
            registrarAsistenciaSinHorario(dniLong, hoy, ahora, "Asignar manualmente por día incorrecto");
            return new RespuestaAsistenciaDTO(true, "⚠ Registrado sin horario. Día incorrecto.");
        }

        HorarioDTO horarioActual = buscarHorarioActual(horariosHoy, ahora);
        if (horarioActual == null) {
            System.out.println("⛔ No se encontró ningún horario activo ahora para este DNI.");
            registrarAsistenciaSinHorario(dniLong, hoy, ahora, "Asignar manualmente por hora incorrecta");
            return new RespuestaAsistenciaDTO(true, "⛔ Registrado sin horario. Hora fuera de tramo.");
        }

        boolean ok = procesarRegistro(hoy, ahora, dniLong, horarioActual, horariosHoy);
        if (ok) {
            return new RespuestaAsistenciaDTO(true, "✅ Asistencia registrada correctamente.");
        } else {
            return new RespuestaAsistenciaDTO(false, "❌ Error al procesar el registro.");
        }
    }

    @Override
    public boolean verificarSiYaEstaRegistrado(LocalDate fecha, Long dni, Integer horarioId) {
        return repository.existsByFechaAndDniAndHorarioId(fecha, dni, horarioId);
    }

    @Override
    public void registrarEntrada(LocalDate fecha, LocalTime ahora, Long dni, HorarioDTO horario) {
//        LocalTime ahora = LocalTime.now();
        LocalTime entradaEsperada = horario.getEntrada();
        int estado = ahora.isBefore(entradaEsperada.plusMinutes(15)) ? 0 : 1; // 0 = Presente, 1 = Tardanza
        AsistenciaPersonal asistencia = new AsistenciaPersonal();
        asistencia.setDni(dni);
        asistencia.setHorarioId(horario.getId());
        asistencia.setFecha(fecha);
        asistencia.setHoraEntrada(ahora);
        asistencia.setEstado(estado);
        repository.save(asistencia);
        System.out.println("✅ Entrada registrada con estado: " + estado);
    }

    @Override
    public boolean marcarHoraSalida(LocalDate fecha, LocalTime ahora, Long dni, HorarioDTO horario) {
        Optional<AsistenciaPersonal> asistenciaOpt = repository.findByFechaAndDniAndHorarioId(fecha, dni, horario.getId());
        if (asistenciaOpt.isEmpty()) return false;
        AsistenciaPersonal asistencia = asistenciaOpt.get();
        LocalTime salidaEsperada = horario.getSalida();
        if (asistencia.getHoraSalida() != null) {
            if (ahora.isAfter(asistencia.getHoraSalida())) {
                asistencia.setHoraSalida(ahora);
                System.out.println("🔁 Hora de salida actualizada a una más tardía: " + ahora);
            } else {
                System.out.println("⚠️ Ya hay una hora de salida registrada más tardía o igual: " + asistencia.getHoraSalida());
                return false;
            }
        } else {
            asistencia.setHoraSalida(ahora);
            System.out.println("✅ Salida registrada: " + ahora);
        }
        if (ahora.isBefore(salidaEsperada.minusMinutes(5))) {
            asistencia.setEstado(2); // 2 = Salida Temprana
        }
        repository.save(asistencia);
        return true;
    }

    private List<HorarioDTO> obtenerHorariosDelDia(String dni, LocalDate fecha) {
        List<HorarioDTO> horarios = horarioService.obtenerPorDni(dni);
        if (horarios == null) return Collections.emptyList();
        String diaActual = traducirDia(fecha.getDayOfWeek());
        return horarios.stream()
                .filter(h -> h.getDia().equalsIgnoreCase(diaActual))
                .sorted(Comparator.comparing(HorarioDTO::getEntrada))
                .toList();
    }

    private HorarioDTO buscarHorarioActual(List<HorarioDTO> horarios, LocalTime ahora) {
        for (HorarioDTO h : horarios) {
            LocalTime entrada = h.getEntrada();
            LocalTime salida = h.getSalida();
            LocalTime rangoInicio = entrada.minusMinutes(15);
            LocalTime rangoFin = salida.plusMinutes(15);
            if (ahora.isAfter(rangoInicio) && ahora.isBefore(rangoFin)) {
                return h;
            }
        }
        return null;
    }

    private boolean procesarRegistro(LocalDate fecha, LocalTime ahora, Long dni, HorarioDTO horarioActual, List<HorarioDTO> horariosHoy) {
        Integer horarioId = horarioActual.getId();
        System.out.println("✅ Horario actual detectado: ID " + horarioId +
                " (" + horarioActual.getEntrada() + " - " + horarioActual.getSalida() + ")");
        repository.findByFechaAndDniAndHorarioId(fecha, dni, horarioId)
                .ifPresentOrElse(asistencia -> {
                    manejarAsistenciaExistente(fecha, ahora, dni, horarioActual, asistencia, horariosHoy);
                }, () -> {
                    registrarEntrada(fecha, ahora, dni, horarioActual);
                    verificarYRegistrarTramosIntermedios(fecha, dni, horarioActual, horariosHoy, ahora);
                });
        return true;
    }

    private void manejarAsistenciaExistente(LocalDate fecha, LocalTime ahora, Long dni, HorarioDTO horarioActual, AsistenciaPersonal asistencia, List<HorarioDTO> horariosHoy) {
        Integer horarioId = horarioActual.getId();
        if (asistencia.getHoraEntrada() == null) {
            this.actualizarAsistencia(horarioId, asistencia);
        } else {
            boolean salidaRegistrada = marcarHoraSalida(fecha, ahora, dni, horarioActual);
            if (!salidaRegistrada) {
                System.out.println("⚠️ Salida ya estaba registrada.");
            }
        }
        verificarYRegistrarTramosIntermedios(fecha, dni, horarioActual, horariosHoy, ahora);
    }
    private void verificarYRegistrarTramosIntermedios(LocalDate fecha, Long dni, HorarioDTO horarioActual, List<HorarioDTO> horariosHoy, LocalTime ahora) {
        try {
            System.out.println("Inicio - verificarYRegistrarTramosIntermedios");
            System.out.println("Fecha: " + fecha + ", DNI: " + dni);

            if (horarioActual == null) {
                System.out.println("Horario actual es null. Abortando.");
                return;
            }
            if (horarioActual.getId() == null) {
                System.out.println("Horario actual ID es null. Abortando.");
                return;
            }
            var lista = repository.findByFechaAndDniOrderByHoraEntradaAsc(fecha, dni);
            if (lista == null) {
                System.out.println("La lista devuelta por repository.findByFechaAndDni es null. Abortando.");
                return;
            }
            System.out.println("Registros encontrados: " + lista.size());

            boolean hayOtraFranjaMarcada = lista.stream()
                    .filter(Objects::nonNull)
                    .peek(a -> System.out.println("Registro: horarioId=" + a.getHorarioId() + ", horaEntrada=" + a.getHoraEntrada()))
                    .anyMatch(a -> {
                        Integer idRegistro = a.getHorarioId();
                        if (idRegistro == null) {
                            System.out.println("HorarioId en registro es null, se omite");
                            System.out.println("DEBUG lista.get(1): horarioId=" + lista.get(1).getHorarioId() +
                                    ", horaEntrada=" + lista.get(1).getHoraEntrada() +
                                    ", asitenciaid=" + lista.get(1).getId() +
                                    ", fecha=" + lista.get(1).getFecha());
//                            H asistenciaPersonal = this.repository.findByHorarioIdAndFecha(lista.get(1).getHorarioId(), fecha);
                            PersonalHorario horario=this.horarioService.findById(lista.get(1).getHorarioId()).get();
                            a.setHoraSalida(horario.getEntrada());
                            this.actualizarAsistencia(a.getId(), a);
                            AsistenciaPersonal asitenciaLast=repository.findById(lista.get(lista.size() - 1).getId()).get();
                            PersonalHorario horariolast=this.horarioService.findById(asitenciaLast.getHorarioId()).get();
                            asitenciaLast.setHoraSalida(asitenciaLast.getHoraEntrada());
                            asitenciaLast.setHoraEntrada(horario.getEntrada());
                            this.actualizarAsistencia(asitenciaLast.getId(), asitenciaLast);
                            return false;
                        }
                        return !idRegistro.equals(horarioActual.getId()) && a.getHoraEntrada() != null;
                    });
            System.out.println("hayOtraFranjaMarcada = " + hayOtraFranjaMarcada);
            if (hayOtraFranjaMarcada) {
                System.out.println("Llamando a registrarTramosIntermedios...");
                registrarTramosIntermedios(fecha, dni, horarioActual, horariosHoy, ahora);
                System.out.println("registrarTramosIntermedios finalizó correctamente.");
            }

            System.out.println("Fin - verificarYRegistrarTramosIntermedios");

        } catch (Exception e) {
            System.err.println("Excepción en verificarYRegistrarTramosIntermedios: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    private void registrarTramosIntermedios(LocalDate fecha, Long dni, HorarioDTO horarioFinal, List<HorarioDTO> horariosHoy,LocalTime ahora) {
        List<HorarioDTO> horariosOrdenados = horariosHoy.stream()
                .sorted(Comparator.comparing(HorarioDTO::getEntrada))
                .collect(Collectors.toList());

        Integer idFinal = horarioFinal.getId();

        // Busca el primer horario registrado distinto del final
        HorarioDTO inicial = horariosOrdenados.stream()
                .filter(h -> !h.getId().equals(idFinal) && verificarSiYaEstaRegistrado(fecha, dni, h.getId()))
                .findFirst()
                .orElse(null);

        if (inicial == null) return;

        // Ordenar para asegurarse que inicial sea menor que final
        if (inicial.getEntrada().isAfter(horarioFinal.getEntrada())) {
            HorarioDTO temp = inicial;
            inicial = horarioFinal;
            horarioFinal = temp;
        }

        LocalTime entradaInicial = inicial.getEntrada();
        LocalTime entradaFinal = horarioFinal.getEntrada();

        System.out.println("🗓️ Horarios del día:");
        horariosOrdenados.forEach(h ->
                System.out.println("ID " + h.getId() + " (" + h.getEntrada() + " - " + h.getSalida() + ")"));

        System.out.println("📦 Detectado tramo desde horario ID " + inicial.getId() +
                " hasta horario ID " + horarioFinal.getId());

        final LocalDate fechaFinal = fecha;
        final Long dniFinal = dni;
        final HorarioDTO inicialFinal = inicial;
        final HorarioDTO finalHorarioFinal = horarioFinal;

        horariosOrdenados.stream()
                .filter(h -> !h.getEntrada().isBefore(entradaInicial) && !h.getEntrada().isAfter(entradaFinal))
                .forEach(h -> procesarHorarioEnTramo(fechaFinal, dniFinal, inicialFinal, finalHorarioFinal, h,ahora));

    }

    private void procesarHorarioEnTramo(LocalDate fecha, Long dni, HorarioDTO inicial, HorarioDTO finalHorario, HorarioDTO h,LocalTime ahora) {
        boolean yaRegistrado = verificarSiYaEstaRegistrado(fecha, dni, h.getId());
        boolean esInicial = h.getId().equals(inicial.getId());
        boolean esFinal = h.getId().equals(finalHorario.getId());

        System.out.println("⏳ Evaluando horario ID " + h.getId() + " con entrada " + h.getEntrada() +
                " - ¿Ya registrado?: " + yaRegistrado + " - ¿Inicial?: " + esInicial + " - ¿Final?: " + esFinal);

        if (esInicial) {
            this.marcarHoraSalida(fecha, h.getSalida(), dni, h);
        }
        if (esFinal) {
            AsistenciaPersonal asistenciaPersonal = this.repository.findByHorarioIdAndFecha(h.getId(), fecha);
            asistenciaPersonal.setHoraEntrada(h.getEntrada());
            asistenciaPersonal.setHoraSalida(ahora);
            asistenciaPersonal.setEstado(0);
            this.actualizarAsistencia(asistenciaPersonal.getId(), asistenciaPersonal);
        }
        if (!esInicial && !esFinal && !yaRegistrado) {
            // Para horarios intermedios, registrar entrada y salida
            registrarEntrada(fecha, h.getEntrada(), dni, h);
            marcarHoraSalida(fecha, h.getSalida(), dni, h);
            System.out.println("✅ Horario intermedio ID " + h.getId() +
                    " (" + h.getEntrada() + " - " + h.getSalida() + ") marcado automáticamente como Presente.");
        }
    }




    private void registrarAsistenciaSinHorario(Long dni, LocalDate fecha, LocalTime hora, String observacion) {
        Optional<AsistenciaPersonal> asistenciaPendiente = repository.findFirstByDniAndFechaAndHoraSalidaIsNullOrderByHoraEntradaAsc(dni, fecha);
        if (asistenciaPendiente.isPresent()) {
            AsistenciaPersonal asistencia = asistenciaPendiente.get();
            asistencia.setHoraSalida(hora);
            repository.save(asistencia);
            System.out.println("✅ Hora de salida registrada automáticamente para el DNI: " + dni);
        } else {
            AsistenciaPersonal nueva = new AsistenciaPersonal();
            nueva.setDni(dni);
            nueva.setFecha(fecha);
            nueva.setHoraEntrada(hora);
            nueva.setHoraSalida(null);
            nueva.setHorarioId(null);
            nueva.setObservaciones(observacion);
            nueva.setEstado(5); // Estado personalizado para ausencia o registro manual
            repository.save(nueva);
            System.out.println("✅ Entrada registrada manualmente (sin horario) para el DNI: " + dni);
        }
    }


    private String traducirDia(DayOfWeek dia) {
        switch (dia) {
            case MONDAY: return "LUNES";
            case TUESDAY: return "MARTES";
            case WEDNESDAY: return "MIERCOLES";
            case THURSDAY: return "JUEVES";
            case FRIDAY: return "VIERNES";
            case SATURDAY: return "SABADO";
            case SUNDAY: return "DOMINGO";
            default: return "";
        }
    }





    @Override
    public List<DetalleAsistenciaPersonalDTO> obtenerDetallePorFecha(LocalDate fecha) {
        String dia = traducirDia(fecha.getDayOfWeek());
        List<Object[]> resultados = repository.obtenerDetallePorDia(fecha, dia);
        List<DetalleAsistenciaPersonalDTO> lista = new ArrayList<>();

        for (Object[] fila : resultados) {
            DetalleAsistenciaPersonalDTO dto = new DetalleAsistenciaPersonalDTO(
                    fila[0] != null ? ((Number) fila[0]).longValue() : null,  // asistenciaId
                    ((Number) fila[1]).longValue(),                           // horarioId
                    (String) fila[2],                                         // carreraId
                    (String) fila[3],                                         // materiaNombre
                    ((Number) fila[4]).longValue(),                           // dni
                    (String) fila[5],                                         // apellido
                    (String) fila[6],                                         // nombre
                    (String) fila[7],                                         // horaEntrada
                    (String) fila[8],                                         // horaSalida
                    (String) fila[9] ,                                         // observaciones
                    (Integer) fila[10],                                           //estado
                    (fila[11] != null) ? ((java.sql.Date) fila[11]).toLocalDate() : null
            );
            lista.add(dto);
        }
        return lista;
    }

    @Override
    public boolean existeAsistenciaParaHoy(Long horarioId, LocalDate fecha) {
        return repository.existsByHorarioIdAndFecha(horarioId, fecha);
    }

    @Override
    public List<DetalleAsistenciaPersonalDTO> obtenerDetallePorYear(Integer Year) {
        List<Object[]> resultados = repository.obtenerDetallePorYear(Year);
        List<DetalleAsistenciaPersonalDTO> lista = new ArrayList<>();

        for (Object[] fila : resultados) {
            DetalleAsistenciaPersonalDTO dto = new DetalleAsistenciaPersonalDTO(
                    fila[0] != null ? ((Number) fila[0]).longValue() : null,  // asistenciaId
                    fila[1] != null ? ((Number) fila[1]).longValue() : null,   // horarioId
                    (String) fila[2],                                         // carreraId
                    (String) fila[3],                                         // materiaNombre
//                    ((Number) fila[4]).longValue(),                           // dni
                    fila[4] != null ? ((Number) fila[4]).longValue() : 0L,
                    (String) fila[5],                                         // apellido
                    (String) fila[6],                                         // nombre
                    (String) fila[7],                                         // horaEntrada
                    (String) fila[8],                                         // horaSalida
                    (String) fila[9] ,                                         // observaciones
                    (Integer) fila[10],                                           //estado
                    (fila[11] != null) ? ((java.sql.Date) fila[11]).toLocalDate() : null                               //felcha
            );
            lista.add(dto);
        }
        return lista;
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

    @Override
    public List<DetalleAsistenciaPersonalDTO> obtenerDetallePorDNI(Long dni) {
        List<Object[]> resultados = repository.obtenerDetallePorDniYAnio(dni,2025);
        List<DetalleAsistenciaPersonalDTO> lista = new ArrayList<>();

        for (Object[] fila : resultados) {
            DetalleAsistenciaPersonalDTO dto = new DetalleAsistenciaPersonalDTO(
                    fila[0] != null ? ((Number) fila[0]).longValue() : null,  // asistenciaId
                    fila[1] != null ? ((Number) fila[1]).longValue() : null,   // horarioId
                    (String) fila[2],                                         // carreraId
                    (String) fila[3],                                         // materiaNombre
//                    ((Number) fila[4]).longValue(),                           // dni
                    fila[4] != null ? ((Number) fila[4]).longValue() : 0L,
                    (String) fila[5],                                         // apellido
                    (String) fila[6],                                         // nombre
                    (String) fila[7],                                         // horaEntrada
                    (String) fila[8],                                         // horaSalida
                    (String) fila[9] ,                                         // observaciones
                    (Integer) fila[10],                                           //estado
                    (fila[11] != null) ? ((java.sql.Date) fila[11]).toLocalDate() : null                               //felcha
            );
            lista.add(dto);
        }
        return lista;
    }

    @Override
    public List<ReporteFaltasDTO> cargarAsistenciasDelMes(int mes) {
        List<Object[]> resultados = repository.obtenerReporteFaltasPorMes(mes);
        List<ReporteFaltasDTO> asistencias = resultados.stream().map(obj -> {
            String nombreCompleto = obj[0] != null ? (String) obj[0] : "";
            String materiaNombre = obj[1] != null ? (String) obj[1] : "";
            String dni = obj[2] != null ? String.valueOf(obj[2]) : "";
            Integer estado0 = obj[3] != null ? ((Number) obj[3]).intValue() : 0;
            Integer estado1 = obj[4] != null ? ((Number) obj[4]).intValue() : 0;
            Integer estado2 = obj[5] != null ? ((Number) obj[5]).intValue() : 0;
            Integer estado3 = obj[6] != null ? ((Number) obj[6]).intValue() : 0;
            Integer totalCount = obj[7] != null ? ((Number) obj[7]).intValue() : 0;
            return new ReporteFaltasDTO(
                    nombreCompleto,
                    materiaNombre,
                    dni,
                    estado0,
                    estado1,
                    estado2,
                    estado3,
                    totalCount
            );
        }).toList();
        return asistencias;
    }

    @Override
    public List<AsistenciaDetalleDTO> obtenerAsistenciasPorFecha(LocalDate fecha) {
        List<AsistenciaDetalleDTO> asistencias = repository.buscarAsistenciasPorFechaYDía(fecha);
        return asistencias;
    }
}