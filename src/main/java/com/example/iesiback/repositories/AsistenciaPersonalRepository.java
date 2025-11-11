package com.example.iesiback.repositories;

import com.example.iesiback.dto.AsistenciaDetalleDTO;
import com.example.iesiback.dto.DetalleAsistenciaPersonalDTO;
import com.example.iesiback.dto.ReporteFaltasDTO;
import com.example.iesiback.entities.AsistenciaPersonal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface  AsistenciaPersonalRepository extends JpaRepository<AsistenciaPersonal, Integer> {
//    @Query("SELECT COUNT(a) > 0 FROM AsistenciaPersonal a WHERE a.fecha = :fecha AND a.dni = :dni AND a.horarioId = :horarioId")
//    boolean existeAsistencia(@Param("fecha") LocalDate fecha, @Param("dni") Long dni, @Param("horarioId") Integer horarioId);

    boolean existsByFechaAndDniAndHorarioId(LocalDate fecha, Long dni, Integer horarioId);

    Optional<AsistenciaPersonal> findByFechaAndDniAndHorarioId(LocalDate fecha, Long dni, Integer horarioId);
//List<AsistenciaPersonal> findByFechaAndDniAndHorarioId(LocalDate fecha, Long dni, Integer horarioId);

    List<AsistenciaPersonal> findByDni(Long dni);
    Optional<AsistenciaPersonal> findFirstByDniAndFechaAndHoraSalidaIsNullOrderByHoraEntradaAsc(Long dni, LocalDate fecha);

    @Query(value = """
        SELECT
            ap.id,
            ph.id,
            mc.carrera_id,
            m.materia_nombre,
            ph.dni,
            p.personal_apellido,
            p.personal_nombre,
            TO_CHAR(ap.hora_entrada, 'HH24:MI'),
            TO_CHAR(ap.hora_salida, 'HH24:MI'),
            ap.observaciones,
            ap.estado,
            ap.fecha
        FROM materia m
        INNER JOIN materia_carrera mc ON m.materia_id = mc.materia_id
        INNER JOIN personal_horarios ph ON mc.id = ph.razon
        INNER JOIN personal p ON ph.dni = p.personal_dni
        INNER JOIN asistencia_personal ap ON ph.id = ap.horario_id AND ap.fecha = CAST(:fecha AS date)
        WHERE (ph.dia = :dia OR ap.id IS NOT NULL)
        ORDER BY p.personal_apellido ASC
        """, nativeQuery = true)
    List<Object[]> obtenerDetallePorDia(@Param("fecha") LocalDate fecha, @Param("dia") String dia);


    @Query(value = """
            SELECT
                ap.id,
                ph.id,
                mc.carrera_id,
                m.materia_nombre,
                ph.dni,
                p.personal_apellido,
                p.personal_nombre,
                TO_CHAR(ap.hora_entrada, 'HH24:MI'),
                TO_CHAR(ap.hora_salida, 'HH24:MI'),
                ap.observaciones,
                ap.estado,
                ap.fecha
            FROM personal_horarios ph
            LEFT JOIN materia_carrera mc ON mc.id = ph.razon
            LEFT JOIN materia m ON m.materia_id = mc.materia_id
            LEFT JOIN personal p ON ph.dni = p.personal_dni
            LEFT JOIN asistencia_personal ap ON ph.id = ap.horario_id
            WHERE ph.year = :year
            
            UNION
            
            -- Parte 2: asistencias sin horario
            SELECT
                ap.id,
                NULL AS horario_id,
                NULL AS carrera_id,
                NULL AS materia_nombre,
                ap.dni,
                p.personal_apellido,
                p.personal_nombre,
                TO_CHAR(ap.hora_entrada, 'HH24:MI'),
                TO_CHAR(ap.hora_salida, 'HH24:MI'),
                ap.observaciones,
                ap.estado,
                ap.fecha
            FROM asistencia_personal ap
            LEFT JOIN personal p ON ap.dni = p.personal_dni
            WHERE ap.horario_id IS NULL
              AND EXTRACT(YEAR FROM ap.fecha) = :year
            """, nativeQuery = true)
    List<Object[]> obtenerDetallePorYear(@Param("year") Integer year);




 boolean existsByHorarioIdAndFecha(Long horarioId, LocalDate fecha);

    List<AsistenciaPersonal> findByHorarioIdAndFecha(Integer horarioId, LocalDate fecha);

    @Query(value = """
    SELECT
        ap.id,
        ph.id AS horario_id,
        mc.carrera_id,
        m.materia_nombre,
        ph.dni,
        p.personal_apellido,
        p.personal_nombre,
        TO_CHAR(ap.hora_entrada, 'HH24:MI'),
        TO_CHAR(ap.hora_salida, 'HH24:MI'),
        ap.observaciones,
        ap.estado,
        ap.fecha
    FROM personal_horarios ph
    LEFT JOIN materia_carrera mc ON mc.id = ph.razon
    LEFT JOIN materia m ON m.materia_id = mc.materia_id
    LEFT JOIN personal p ON ph.dni = p.personal_dni
    LEFT JOIN asistencia_personal ap ON ph.id = ap.horario_id
    WHERE ph.year = :year
      AND ph.dni = :dni
UNION
    SELECT
        ap.id,
        NULL AS horario_id,
        NULL AS carrera_id,
        NULL AS materia_nombre,
        ap.dni,
        p.personal_apellido,
        p.personal_nombre,
        TO_CHAR(ap.hora_entrada, 'HH24:MI'),
        TO_CHAR(ap.hora_salida, 'HH24:MI'),
        ap.observaciones,
        ap.estado,
        ap.fecha
    FROM asistencia_personal ap
    LEFT JOIN personal p ON ap.dni = p.personal_dni
    WHERE ap.horario_id IS NULL
      AND EXTRACT(YEAR FROM ap.fecha) = :year
      AND ap.dni = :dni
ORDER BY 12 DESC
""", nativeQuery = true)
    List<Object[]> obtenerDetallePorDniYAnio(@Param("dni") Long dni, @Param("year") int year);


    List<AsistenciaPersonal> findByFechaAndDniOrderByHoraEntradaAsc(LocalDate fecha, Long dni);

    @Query(value = """
    SELECT 
        CONCAT(p.personal_apellido, ' ', p.personal_nombre) AS nombreCompleto,
        m.materia_nombre AS materiaNombre,
        ap.dni AS dni,
        SUM(CASE WHEN ap.estado = 0 THEN 1 ELSE 0 END) AS estado0,
        SUM(CASE WHEN ap.estado = 1 THEN 1 ELSE 0 END) AS estado1,
        SUM(CASE WHEN ap.estado = 2 THEN 1 ELSE 0 END) AS estado2,
        SUM(CASE WHEN ap.estado = 3 THEN 1 ELSE 0 END) AS estado3,
        COUNT(*) AS totalCount
    FROM asistencia_personal ap
    INNER JOIN personal_horarios ph ON ph.id = ap.horario_id
    INNER JOIN personal p ON ap.dni = p.personal_dni
    INNER JOIN materia_carrera mc ON ph.razon = mc.id
    INNER JOIN materia m ON mc.materia_id = m.materia_id
    WHERE ap.estado IN (0, 1, 2, 3)
      AND EXTRACT(MONTH FROM ap.fecha) = :mes
      AND EXTRACT(YEAR FROM ap.fecha) = EXTRACT(YEAR FROM CURRENT_DATE)
    GROUP BY nombreCompleto, materiaNombre, ap.dni
    ORDER BY nombreCompleto
""", nativeQuery = true)
    List<Object[]> obtenerReporteFaltasPorMes(@Param("mes") int mes);



    @Query(value = """
    SELECT 
        CONCAT(p.personal_apellido, ' ', p.personal_nombre) AS nombreCompleto,
        m.materia_nombre AS materiaNombre,
        ap.dni AS dni,
        SUM(CASE WHEN ap.estado = 0 THEN 1 ELSE 0 END) AS estado0,
        SUM(CASE WHEN ap.estado = 1 THEN 1 ELSE 0 END) AS estado1,
        SUM(CASE WHEN ap.estado = 2 THEN 1 ELSE 0 END) AS estado2,
        SUM(CASE WHEN ap.estado = 3 THEN 1 ELSE 0 END) AS estado3,
        SUM(CASE WHEN ap.estado = 4 THEN 1 ELSE 0 END) AS estado4,
        COUNT(*) AS totalCount
    FROM asistencia_personal ap
    INNER JOIN personal_horarios ph ON ph.id = ap.horario_id
    INNER JOIN personal p ON ap.dni = p.personal_dni
    INNER JOIN materia_carrera mc ON ph.razon = mc.id
    INNER JOIN materia m ON mc.materia_id = m.materia_id
    WHERE ap.estado IN (0, 1, 2, 3, 4)
      AND ap.fecha BETWEEN :fechaInicio AND :fechaFin
    GROUP BY nombreCompleto, materiaNombre, ap.dni
    ORDER BY nombreCompleto
""", nativeQuery = true)
    List<Object[]> obtenerReporteFaltasPorRango(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );









//    @Query(value = """
//    SELECT
//        ap.id,
//        ph.id,
//        mc.carrera_id,
//        m.materia_nombre,
//        ph.dni,
//        p.personal_apellido,
//        p.personal_nombre,
//        ap.hora_entrada,
//        ap.hora_salida,
//        ap.observaciones
//    FROM materia m
//    INNER JOIN materia_carrera mc ON m.materia_id = mc.materia_id
//    INNER JOIN personal_horarios ph ON mc.id = ph.razon
//    INNER JOIN personal p ON ph.dni = p.personal_dni
//    LEFT JOIN asistencia_personal ap ON ph.id = ap.horario_id AND CAST(ap.fecha AS date) = CAST(:fecha AS date)
//    ORDER BY ph.entrada ASC
//""", nativeQuery = true)
//    List<AsistenciaDetalleDTO> buscarAsistenciasPorFechaYDía(@Param("fecha") LocalDate fecha);


//    @Query(value = """
//    SELECT
//        ap.id AS asistenciaId,
//        ph.id AS horarioId,
//        mc.carrera_id AS carreraId,
//        m.materia_nombre AS materiaNombre,
//        ph.dni AS dni,
//        p.personal_apellido AS apellido,
//        p.personal_nombre AS nombre,
//        ap.hora_entrada AS horaEntrada,
//        ap.hora_salida AS horaSalida,
//        ap.observaciones AS observaciones
//    FROM materia m
//    INNER JOIN materia_carrera mc ON m.materia_id = mc.materia_id
//    INNER JOIN personal_horarios ph ON mc.id = ph.razon
//    INNER JOIN personal p ON ph.dni = p.personal_dni
//    LEFT JOIN asistencia_personal ap
//           ON ph.id = ap.horario_id
//          AND CAST(ap.fecha AS date) = CAST(:fecha AS date)
//    ORDER BY ph.entrada ASC
//""", nativeQuery = true)
//    List<Object[]> buscarAsistenciasPorFechaYDía(@Param("fecha") LocalDate fecha);

    @Query(value = """
    SELECT * FROM (
        SELECT 
            ap.id AS asistenciaId,
            ph.id AS horarioId,
            mc.carrera_id AS carreraId,
            m.materia_nombre AS materiaNombre,
            ph.dni AS dni,
            p.personal_apellido AS apellido,
            p.personal_nombre AS nombre,
            TO_CHAR(ap.hora_entrada, 'HH24:MI') AS horaEntrada,
            TO_CHAR(ap.hora_salida, 'HH24:MI') AS horaSalida,
            ap.observaciones AS observaciones, ap.estado
        FROM materia m
        INNER JOIN materia_carrera mc ON m.materia_id = mc.materia_id
        INNER JOIN personal_horarios ph ON mc.id = ph.razon
        INNER JOIN personal p ON ph.dni = p.personal_dni
        INNER JOIN asistencia_personal ap 
               ON ph.id = ap.horario_id 
              AND ap.fecha::date = :fecha

        UNION ALL

        SELECT 
            ap.id AS asistenciaId,
            0 AS horarioId,              
            'PERSONAL' AS carreraId,     
            'ASISTENCIA PERSONAL' AS materiaNombre, 
            ap.dni AS dni,
            p.personal_apellido AS apellido,
            p.personal_nombre AS nombre,
            TO_CHAR(ap.hora_entrada, 'HH24:MI') AS horaEntrada,
            TO_CHAR(ap.hora_salida, 'HH24:MI') AS horaSalida,
            ap.observaciones AS observaciones,
            ap.estado
        FROM asistencia_personal ap
        INNER JOIN personal p ON ap.dni = p.personal_dni
        WHERE ap.fecha::date = :fecha
          AND ap.horario_id IS NULL
    ) AS t
    ORDER BY horaEntrada ASC
""", nativeQuery = true)
    List<Object[]> buscarAsistenciasPorFechaYDía(@Param("fecha") LocalDate fecha);







//    @Query("SELECT a FROM AsistenciaPersonal a WHERE a.dni = :dni ORDER BY a.horaEntrada DESC LIMIT 1")
//    Optional<AsistenciaPersonal> findUltimoRegistroPorDni(@Param("dni") Long dni);

    List<AsistenciaPersonal> findByDniAndFecha(Long dni, LocalDate fecha);

    Optional<AsistenciaPersonal> findTopByDniAndFechaOrderByHoraEntradaDesc(Long dni, LocalDate fecha);


    @Modifying
    @Transactional
    @Query("UPDATE AsistenciaPersonal a SET a.observaciones = :observaciones, a.estado = :estado WHERE a.fecha = :fecha")
    int actualizarPorFecha(String observaciones, String estado, LocalDate fecha);
}



