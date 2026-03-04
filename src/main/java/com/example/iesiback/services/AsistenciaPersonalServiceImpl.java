package com.example.iesiback.services;

import com.example.iesiback.dto.*;
import com.example.iesiback.entities.AsistenciaPersonal;
import com.example.iesiback.entities.PersonalHorario;
import com.example.iesiback.repositories.AsistenciaPersonalRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    private void verificarHorariosNoMarcados(LocalDate fecha, Long dni, List<HorarioDTO> horariosHoy, LocalTime ahora) {
        System.out.println("1-verificarHorariosNoMarcados");
        for (HorarioDTO horario : horariosHoy) {
            boolean yaRegistrado = verificarSiYaEstaRegistrado(fecha, dni, horario.getId());
            if (!yaRegistrado && horario.getEntrada().isBefore(ahora)) {
                // Registrar ausencia
                AsistenciaPersonal asistencia = new AsistenciaPersonal();
                asistencia.setDni(dni);
                asistencia.setHorarioId(horario.getId());
                asistencia.setFecha(fecha);
                asistencia.setObservaciones("Automarcado, intermedio");
                asistencia.setHoraEntrada(horario.getEntrada());
                asistencia.setHoraSalida(horario.getSalida());
                asistencia.setEstado(0); // 3 = Ausente
                repository.save(asistencia);
            }
        }
    }

    @Override
    public RespuestaAsistenciaDTO AsistenciaDahua(RegistroAsistenciaDTO asistencia) {
        System.out.println("2-AsistenciaDahua");
        String dni = asistencia.getDni();
        String fechaHoraStr = asistencia.getFechaHora();
        String dispositivo = asistencia.getDispositivo();
        Long dniLong;
        System.out.println("=== SOLICITUD RECIBIDA ===");
        System.out.println("DNI       : " + dni);
        System.out.println("Fecha/Hora: " + fechaHoraStr);
        System.out.println("===========================");

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

        Optional<AsistenciaPersonal> ultima = repository.findTopByDniAndFechaOrderByHoraEntradaDesc(dniLong,hoy);
        if (ultima.isPresent() && ultima.get().getHoraEntrada() != null) {
            Duration diff = Duration.between(ultima.get().getHoraEntrada(), fechaHoraCompleta.toLocalTime());
            if (diff.getSeconds() < 180) {
                return new RespuestaAsistenciaDTO(false, "⚠️ Registro duplicado muy reciente (menos de 60 segundos).");
            }
        }

        List<HorarioDTO> horariosHoy = obtenerHorariosDelDia(dni, hoy);

        if (horariosHoy.isEmpty()) {
            registrarAsistenciaSinHorario(dniLong, hoy, ahora, "Asignar manualmente por día incorrecto");
            return new RespuestaAsistenciaDTO(true, "⚠ Registrado sin horario. Día incorrecto.");
        }

        HorarioDTO horarioActual = buscarHorarioActual(horariosHoy, ahora);

        if (horarioActual == null) {
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
        System.out.println("3-verificarSiYaEstaRegistrado");
        return repository.existsByFechaAndDniAndHorarioId(fecha, dni, horarioId);
    }

    @Override
    public void registrarEntrada(LocalDate fecha, LocalTime ahora, Long dni, HorarioDTO horario) {
        System.out.println("4-registrarEntrada");
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
    }

    @Override
    public void registrarEntradaIntermedia(LocalDate fecha, LocalTime ahora, Long dni, HorarioDTO horario) {
        System.out.println("5-registrarEntradaIntermedia");
//        LocalTime ahora = LocalTime.now();
        LocalTime entradaEsperada = horario.getEntrada();
        int estado = 0;
        AsistenciaPersonal asistencia = new AsistenciaPersonal();
        asistencia.setDni(dni);
        asistencia.setHorarioId(horario.getId());
        asistencia.setFecha(fecha);
        asistencia.setHoraEntrada(ahora);
        asistencia.setEstado(estado);
        repository.save(asistencia);
    }

    @Override
    public boolean marcarHoraSalida(LocalDate fecha, LocalTime ahora, Long dni, HorarioDTO horario) {
        System.out.println("6-marcarHoraSalida");
        Optional<AsistenciaPersonal> asistenciaOpt = repository.findByFechaAndDniAndHorarioId(fecha, dni, horario.getId());
        if (asistenciaOpt.isEmpty()) return false;
        AsistenciaPersonal asistencia = asistenciaOpt.get();
        LocalTime salidaEsperada = horario.getSalida();
        if (asistencia.getHoraSalida() != null) {
            if (ahora.isAfter(asistencia.getHoraSalida())) {
                asistencia.setHoraSalida(ahora);
            } else {
                return false;
            }
        } else {
            asistencia.setHoraSalida(ahora);
        }
        if (ahora.isBefore(salidaEsperada.minusMinutes(15))) {
            asistencia.setEstado(2); // 2 = Salida Temprana
        }
        repository.save(asistencia);
        return true;
    }

    private List<HorarioDTO> obtenerHorariosDelDia(String dni, LocalDate fecha) {
        System.out.println("7-obtenerHorariosDelDia");
        List<HorarioDTO> horarios = horarioService.obtenerPorDni(dni);
        if (horarios == null) return Collections.emptyList();
        String diaActual = traducirDia(fecha.getDayOfWeek());
        return horarios.stream()
                .filter(h -> h.getDia().equalsIgnoreCase(diaActual))
                .sorted(Comparator.comparing(HorarioDTO::getEntrada))
                .toList();
    }

    private HorarioDTO buscarHorarioActual(List<HorarioDTO> horarios, LocalTime ahora) {
        System.out.println("8-buscarHorarioActual");
        for (HorarioDTO h : horarios) {
            LocalTime entrada = h.getEntrada();
            LocalTime salida = h.getSalida();
            LocalTime rangoInicio = entrada.minusMinutes(30);
            LocalTime rangoFin = salida.plusMinutes(30);
            if (ahora.isAfter(rangoInicio) && ahora.isBefore(rangoFin)) {
                return h;
            }
        }
        return horarios.stream()
                .filter(h -> h.getSalida().isBefore(ahora))
                .max(Comparator.comparing(HorarioDTO::getSalida))
                .orElse(null);
    }

    private boolean procesarRegistro(LocalDate fecha, LocalTime ahora, Long dni, HorarioDTO horarioActual, List<HorarioDTO> horariosHoy) {
        System.out.println("9-procesarRegistro");
        Integer horarioId = horarioActual.getId();
        repository.findByFechaAndDniAndHorarioId(fecha, dni, horarioId)
                .ifPresentOrElse(asistencia -> {
                    manejarAsistenciaExistente(fecha, ahora, dni, horarioActual, asistencia, horariosHoy);
                }, () -> {
                    // Registro de entrada
                    registrarEntrada(fecha, ahora, dni, horarioActual);
                    verificarYRegistrarTramosIntermedios(fecha, dni, horarioActual, horariosHoy, ahora);
                });
        verificarHorariosNoMarcados(fecha, dni, horariosHoy, ahora);
        return true;
    }


    private void manejarAsistenciaExistente(LocalDate fecha, LocalTime ahora, Long dni, HorarioDTO horarioActual, AsistenciaPersonal asistencia, List<HorarioDTO> horariosHoy) {
        System.out.println("10-manejarAsistenciaExistente");
        Integer horarioId = horarioActual.getId();
        if (asistencia.getHoraEntrada() == null) {
            this.actualizarAsistencia(horarioId, asistencia);
        } else {
            boolean salidaRegistrada = marcarHoraSalida(fecha, ahora, dni, horarioActual);
            if (!salidaRegistrada) {
            }
        }
        verificarYRegistrarTramosIntermedios(fecha, dni, horarioActual, horariosHoy, ahora);
    }


private void verificarYRegistrarTramosIntermedios(LocalDate fecha, Long dni, HorarioDTO horarioActual, List<HorarioDTO> horariosHoy, LocalTime ahora) {
    System.out.println("11-verificarYRegistrarTramosIntermedios");
    try {
        if (horarioActual == null) {
            return;
        }
        if (horarioActual.getId() == null) {
            return;
        }

        var lista = repository.findByFechaAndDniOrderByHoraEntradaAsc(fecha, dni);
        if (lista == null) {
            return;
        }

        boolean hayOtraFranjaMarcada = lista.stream()
                .filter(Objects::nonNull)
                .anyMatch(a -> {
                    Integer idRegistro = a.getHorarioId();
                    if (idRegistro == null) {
                        try {
                            System.out.println("➡️ Buscando horario con id=" + lista.get(1).getHorarioId());
                            PersonalHorario horario = this.horarioService.findById(lista.get(1).getHorarioId()).get();

                            a.setHoraSalida(horario.getEntrada());
                            System.out.println("➡️ Actualizando asistencia id=" + a.getId());
                            this.actualizarAsistencia(a.getId(), a);

                            System.out.println("➡️ Buscando última asistencia id=" + lista.get(lista.size() - 1).getId());
                            AsistenciaPersonal asitenciaLast = repository.findById(lista.get(lista.size() - 1).getId()).get();

                            System.out.println("➡️ Buscando horario de última asistencia con id=" + asitenciaLast.getHorarioId());
                            PersonalHorario horariolast = this.horarioService.findById(asitenciaLast.getHorarioId()).get();

                            asitenciaLast.setHoraSalida(asitenciaLast.getHoraEntrada());
                            asitenciaLast.setHoraEntrada(horario.getEntrada());
                            asitenciaLast.setEstado(0);
                            System.out.println("➡️ Actualizando última asistencia id=" + asitenciaLast.getId());
                            this.actualizarAsistencia(asitenciaLast.getId(), asitenciaLast);

                        } catch (Exception ex) {
                            System.err.println("❌ ERROR dentro de anyMatch: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                        return false;
                    }
                    return !idRegistro.equals(horarioActual.getId()) && a.getHoraEntrada() != null;
                });

        if (hayOtraFranjaMarcada) {
            registrarTramosIntermedios(fecha, dni, horarioActual, horariosHoy, ahora);
        }
    } catch (Exception e) {
        System.err.println("Excepción en verificarYRegistrarTramosIntermedios: " + e.getMessage());
        e.printStackTrace();
        throw e; // <- por eso el stacktrace apunta a la última línea
    }
}



    private void registrarTramosIntermedios(LocalDate fecha, Long dni, HorarioDTO horarioFinal, List<HorarioDTO> horariosHoy,LocalTime ahora) {
        System.out.println("12-registrarTramosIntermedios");
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

        horariosOrdenados.forEach(h ->
                System.out.println("ID " + h.getId() + " (" + h.getEntrada() + " - " + h.getSalida() + ")"));


        final LocalDate fechaFinal = fecha;
        final Long dniFinal = dni;
        final HorarioDTO inicialFinal = inicial;
        final HorarioDTO finalHorarioFinal = horarioFinal;

        horariosOrdenados.stream()
                .filter(h -> !h.getEntrada().isBefore(entradaInicial) && !h.getEntrada().isAfter(entradaFinal))
                .forEach(h -> procesarHorarioEnTramo(fechaFinal, dniFinal, inicialFinal, finalHorarioFinal, h,ahora));

    }

    private void procesarHorarioEnTramo(LocalDate fecha, Long dni, HorarioDTO inicial, HorarioDTO finalHorario, HorarioDTO h, LocalTime ahora) {
        System.out.println("13-procesarHorarioEnTramo");
        boolean yaRegistrado = verificarSiYaEstaRegistrado(fecha, dni, h.getId());
        boolean esInicial = h.getId().equals(inicial.getId());
        boolean esFinal = h.getId().equals(finalHorario.getId());

        if (esInicial) {
            this.marcarHoraSalida(fecha, h.getSalida(), dni, h);
        }

        if (esFinal) {
            // Buscar todos los registros que coincidan
            List<AsistenciaPersonal> asistencias = this.repository.findByHorarioIdAndFecha(h.getId(), fecha);

            if (asistencias != null && !asistencias.isEmpty()) {
                // Tomamos el último (el más reciente) para actualizar
                AsistenciaPersonal asistenciaPersonal = asistencias.get(asistencias.size() - 1);

                asistenciaPersonal.setHoraEntrada(h.getEntrada());
                asistenciaPersonal.setHoraSalida(ahora);
                asistenciaPersonal.setEstado(0);

                this.actualizarAsistencia(asistenciaPersonal.getId(), asistenciaPersonal);
            } else {
                System.out.println("⚠️ No se encontró asistencia para horarioId=" + h.getId() + " en fecha=" + fecha);
            }
        }

        if (!esInicial && !esFinal && !yaRegistrado) {
            // Para horarios intermedios, registrar entrada y salida
            registrarEntradaIntermedia(fecha, h.getEntrada(), dni, h);
            marcarHoraSalida(fecha, h.getSalida(), dni, h);
        }
    }


    private void registrarAsistenciaSinHorario(Long dni, LocalDate fecha, LocalTime hora, String observacion) {
        System.out.println("14-registrarAsistenciaSinHorario");
        Optional<AsistenciaPersonal> asistenciaPendiente = repository.findFirstByDniAndFechaAndHoraSalidaIsNullOrderByHoraEntradaAsc(dni, fecha);
        if (asistenciaPendiente.isPresent()) {
            AsistenciaPersonal asistencia = asistenciaPendiente.get();
            asistencia.setHoraSalida(hora);
            repository.save(asistencia);
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
        }
    }


    @Override
    public void actualizarAsistencia(Integer id, AsistenciaPersonal update) {
        System.out.println("15-actualizarAsistencia");
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


    private String traducirDia(DayOfWeek dia) {
        System.out.println("16-traducirDia");
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
        System.out.println("17-obtenerDetallePorFecha");
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
public List<AsistenciaPersonal> obtenerPorDniYFecha(Long dni, LocalDate fecha) {
        return repository.findByDniAndFecha(dni, fecha);
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
    public List<DetalleAsistenciaPersonalDTO> obtenerDetallePorDNI(Long dni) {
        int anioActual = LocalDate.now().getYear();
        List<Object[]> resultados = repository.obtenerDetallePorDniYAnio(dni, anioActual);
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


//@Override
//public List<DetalleAsistenciaPersonalDTO> obtenerDetallePorDNI(Long dni) {
//
//    List<DetalleAsistenciaPersonalDTO> lista = new ArrayList<>();
//
//    int anio = LocalDate.now().getYear();
//
//
//
//    LocalDate inicio = LocalDate.of(anio, 1, 1);
//    LocalDate fin = LocalDate.now();
//
//    Random random = new Random();
//    LocalDate fecha = inicio;
//
//    while (!fecha.isAfter(fin)) {
//
//        DayOfWeek day = fecha.getDayOfWeek();
//
//        // ❌ Saltar sábado y domingo
//        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
//            fecha = fecha.plusDays(1);
//            continue;
//        }
//
//        // 🔥 Obtener feriados con motivo desde API gobierno
//        Map<LocalDate, String> feriados = feriadosService.obtenerFeriadosConMotivo(anio);
//        // 🎉 Día feriado -> agregar registro especial
//        if (feriados.containsKey(fecha)) {
//
//            String motivo = feriados.get(fecha);
//
//            DetalleAsistenciaPersonalDTO dtoFeriado = new DetalleAsistenciaPersonalDTO(
//                    null,       // asistenciaId
//                    null,       // horarioId
//                    "-",       // carreraId
//                    "-",       // materiaNombre
//                    dni,        // dni real
//                    "FERIADO",  // apellido
//                    motivo,     // nombre = motivo del feriado
//                    "-",       // horaEntrada
//                    "-",       // horaSalida
//                    motivo,  // observaciones
//                    8,          // estado 0 = no laboral
//                    fecha       // fecha del feriado
//            );
//
//            lista.add(dtoFeriado);
//            fecha = fecha.plusDays(1);
//            continue;
//        }
//
//        // ✔ Día normal -> generar asistencia falsa
//        int vEntrada = random.nextInt(41) - 20;
//        int vSalida = random.nextInt(41) - 20;
//
//        LocalTime entrada = LocalTime.of(14, 0).plusMinutes(vEntrada);
//        LocalTime salida = LocalTime.of(20, 0).plusMinutes(vSalida);
//
//        DetalleAsistenciaPersonalDTO dto = new DetalleAsistenciaPersonalDTO(
//                random.nextLong(1_000_000),
//                random.nextLong(1_000_000),
//                "TUR-01",
//                "Asistencia Personal",
//                dni,
//                "Pérez",
//                "Juan",
//                entrada.toString(),
//                salida.toString(),
//                "",
//                0,
//                fecha
//        );
//
//        lista.add(dto);
//
//        fecha = fecha.plusDays(1);
//    }
//
//    return lista;
//}


    @Override
    public List<ReporteFaltasDTO> cargarAsistenciasDelMesRango(LocalDate fechaInicio, LocalDate fechaFin) {
//    public List<ReporteFaltasDTO> cargarAsistenciasDelMes(int mes) {
//        List<Object[]> resultados = repository.obtenerReporteFaltasPorMes(mes);
        List<Object[]> resultados = repository.obtenerReporteFaltasPorRango(fechaInicio, fechaFin);
        List<ReporteFaltasDTO> asistencias = resultados.stream().map(obj -> {
            String nombreCompleto = obj[0] != null ? (String) obj[0] : "";
            String materiaNombre = obj[1] != null ? (String) obj[1] : "";
            String dni = obj[2] != null ? String.valueOf(obj[2]) : "";
            Integer estado0 = obj[3] != null ? ((Number) obj[3]).intValue() : 0;
            Integer estado1 = obj[4] != null ? ((Number) obj[4]).intValue() : 0;
            Integer estado2 = obj[5] != null ? ((Number) obj[5]).intValue() : 0;
            Integer estado3 = obj[6] != null ? ((Number) obj[6]).intValue() : 0;
            Integer estado4 = obj[7] != null ? ((Number) obj[7]).intValue() : 0;
            Integer totalCount = obj[8] != null ? ((Number) obj[8]).intValue() : 0;
            return new ReporteFaltasDTO(
                    nombreCompleto,
                    materiaNombre,
                    dni,
                    estado0,
                    estado1,
                    estado2,
                    estado3,
                    estado4,
                    totalCount
            );
        }).toList();
        return asistencias;
    }




    @Override
    public List<AsistenciaDetalleDTO> obtenerAsistenciasPorFecha(LocalDate fecha) {
        List<Object[]> result = repository.buscarAsistenciasPorFechaYDía(fecha);

        List<AsistenciaDetalleDTO> asistencias = new ArrayList<>();

        for (Object[] r : result) {
            try {
                String horaEntrada = r[7] != null ? r[7].toString() : null;
                String horaSalida  = r[8] != null ? r[8].toString() : null;
                Integer estado = r[10] != null ? (Integer) r[10] : null;
                String estadoTexto = estado != null ? getEstadoTexto(estado) : "Desconocido";
                AsistenciaDetalleDTO dto = new AsistenciaDetalleDTO(
                        r[0] != null ? ((Integer) r[0]).longValue() : null,  // asistenciaId
                        r[1] != null ? ((Integer) r[1]).longValue() : null,  // horarioId
                        (String) r[2],                                       // carreraId
                        (String) r[3],                                       // materiaNombre
                        r[4] != null ? String.valueOf(r[4]) : null,          // dni
                        (String) r[5],                                       // apellido
                        (String) r[6],                                       // nombre
                        horaEntrada,                                         // horaEntrada
                        horaSalida,                                          // horaSalida
                        (String) r[9],                                        // observaciones
                        estadoTexto                                          // estado como texto                                    // observaciones
                );
                asistencias.add(dto);
            } catch (ClassCastException e) {
                System.err.println("❌ Error de casteo en fila: " + Arrays.toString(r));
                for (int i = 0; i < r.length; i++) {
                    System.err.println("  -> r[" + i + "] = " + r[i] + " (tipo: " + (r[i] != null ? r[i].getClass() : "null") + ")");
                }
                throw e;
            }
        }

        return asistencias;
    }

    public String getEstadoTexto(Integer estado) {
        if (estado == null) {
            return "Desconocido";
        }

        switch (estado) {
            case 0:
                return "Presente";
            case 1:
                return "Tardanza";
            case 2:
                return "Salida Temprana";
            case 3:
                return "Falta Justificada";
            case 4:
                return "Falta Injustificada";
            case 5:
                return "Requiere Informacion";
            case 6:
                return "MESA DE EXAMEN";
            case 7:
                return "JORNADA INSTITUCIONAL";
            case 8:
                return "FERIADO";
            case 9:
                return "SUSPENSION DE ACTIVIDADES";
            default:
                return "Desconocido";
        }
    }

    @Transactional
    @Override
    public int actualizarObservacionesYEstadoPorFecha(String observaciones, String estado, LocalDate fecha) {
        return repository.actualizarPorFecha(observaciones, estado, fecha);
    }

}

