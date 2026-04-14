package com.example.iesiback.repositories;

import com.example.iesiback.dto.PersonaDTO;
import com.example.iesiback.entities.Persona;
import com.example.iesiback.entities.Personal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonalRepository extends JpaRepository<Personal, String> {
    boolean existsById(String id);

    List<Personal> findByPersonalNombreContainingIgnoreCase(String personalNombre);

    List<Personal> findByPersonalApellidoContainingIgnoreCase(String personalApellido);

    List<Personal> findByPersonalCorreoContainingIgnoreCase(String personalCorreo);

    List<Personal> findByPersonalCelularContainingIgnoreCase(String personalCelular);

    List<Personal> findByPersonalTipoContainingIgnoreCase(String personalTipo);

    List<Personal> findByPersonalRdni(Boolean personalRdni);

    List<Personal> findByPersonalRresidencia(Boolean personalRresidencia);

    List<Personal> findByPersonalRplanilla(Boolean personalRplanilla);

    List<Personal> findByPersonalRsanitario(Boolean personalRsanitario);

    List<Personal> findByPersonalRnacimiento(Boolean personalRnacimiento);

    List<Personal> findByDeclaracion(Boolean declaracion);

    List<Personal> findByCargo(Boolean cargo);

    List<Personal> findByCuil(Boolean cuil);

    List<Personal> findByTitulo(Boolean titulo);

    List<Personal> findByCurriculum(Boolean curriculum);

    @Query("SELECT p FROM Personal p WHERE " +
            "LOWER(p.personalNombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(p.personalApellido) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(CONCAT(p.personalNombre, ' ', p.personalApellido)) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Personal> buscarPorTextoLibre(@Param("texto") String texto);


//    @Query(value = """
//SELECT persona.*
//FROM persona
//INNER JOIN personal ON persona.persona_dni = personal.personal_dni
//WHERE personal.personal_tipo = :tipo
//ORDER BY persona.persona_apellido, persona.persona_nombre
//""", nativeQuery = true)
//    List<Persona> findPersonalOrdenado(@Param("tipo") String tipo);


//    @Query(value = """
//
//            SELECT
//    persona.persona_dni AS personaDni,
//    persona.persona_apellido AS personaApellido,
//    persona.persona_nombre AS personaNombre,
//    persona.persona_correo AS personaCorreo,
//    persona.persona_celular AS personaDomicilioCelular
//FROM persona
//INNER JOIN personal
//    ON persona.persona_dni = personal.personal_dni
//WHERE personal.personal_tipo = :tipo
//ORDER BY persona.persona_apellido, persona.persona_nombre
//""", nativeQuery = true)
//    List<PersonaDTO> findPersonalOrdenado(@Param("tipo") String tipo);
//    }

    @Query("""

            SELECT new com.example.iesiback.dto.PersonaDTO(
    p.personaDni,
    p.personaApellido,
    p.personaNombre,
    p.personaCorreo,
    p.personaDomicilioCelular
)
FROM Persona p
JOIN Personal pe ON p.personaDni = pe.id
WHERE pe.personalTipo = :tipo
ORDER BY p.personaApellido, p.personaNombre
""")
    List<PersonaDTO> findPersonalOrdenado(@Param("tipo") String tipo);

    }