package com.example.iesiback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTOs del flujo de preinscripción (público y presencial).
 * Ninguno expone entidades ni datos personales más allá de lo imprescindible.
 */
public final class PreinscripcionDtos {

    private PreinscripcionDtos() {}

    /** Lo único que el formulario público puede enviar. Todos los datos son obligatorios. */
    public record PreinscripcionPublicaRequest(

            @NotBlank(message = "Ingresá tu DNI.")
            @Pattern(regexp = "\\d{7,8}", message = "El DNI debe tener 7 u 8 dígitos, sin puntos.")
            String dni,

            @NotBlank(message = "Ingresá tu apellido.")
            @Size(max = 100, message = "El apellido es demasiado largo.")
            @Pattern(regexp = "[\\p{L} '.\\-]+", message = "El apellido contiene caracteres no válidos.")
            String apellido,

            @NotBlank(message = "Ingresá tu nombre.")
            @Size(max = 100, message = "El nombre es demasiado largo.")
            @Pattern(regexp = "[\\p{L} '.\\-]+", message = "El nombre contiene caracteres no válidos.")
            String nombre,

            // 50 = largo de persona_correo
            @NotBlank(message = "Ingresá tu correo.")
            @Email(message = "El correo no es válido.")
            @Size(max = 50, message = "El correo no puede superar los 50 caracteres.")
            String correo,

            @NotBlank(message = "Ingresá tu celular.")
            @Pattern(regexp = "[0-9+()\\- ]{8,20}", message = "El celular no es válido.")
            String celular,

            @NotNull(message = "Seleccioná un curso.")
            @Positive(message = "Seleccioná un curso.")
            Integer ofertaId
    ) {}

    /**
     * Preinscripción cargada en ventanilla por personal autenticado.
     * Solo DNI y curso son obligatorios: si la Persona ya existe se usan sus datos oficiales.
     * Si no existe, el servicio exige apellido, nombre y celular (el correo es opcional).
     */
    public record PreinscripcionPresencialRequest(

            @NotBlank(message = "Ingresá el DNI.")
            @Pattern(regexp = "\\d{7,8}", message = "El DNI debe tener 7 u 8 dígitos, sin puntos.")
            String dni,

            @Size(max = 100, message = "El apellido es demasiado largo.")
            @Pattern(regexp = "[\\p{L} '.\\-]*", message = "El apellido contiene caracteres no válidos.")
            String apellido,

            @Size(max = 100, message = "El nombre es demasiado largo.")
            @Pattern(regexp = "[\\p{L} '.\\-]*", message = "El nombre contiene caracteres no válidos.")
            String nombre,

            @Email(message = "El correo no es válido.")
            @Size(max = 50, message = "El correo no puede superar los 50 caracteres.")
            String correo,

            @Pattern(regexp = "([0-9+()\\- ]{8,20})?", message = "El celular no es válido.")
            String celular,

            @NotNull(message = "Seleccioná un curso.")
            @Positive(message = "Seleccioná un curso.")
            Integer ofertaId
    ) {}

    /** Respuesta al crear la preinscripción pública. No se expone el id interno del trámite. */
    public record PreinscripcionCreadaResponse(
            String codigoSeguimiento,
            String estado
    ) {}

    public record IniciarPagoResponse(
            String preferenceId,
            @JsonProperty("init_point") String initPoint
    ) {}

    /** Estado público del proceso: sin datos personales. */
    public record PreinscripcionEstadoResponse(
            String codigoSeguimiento,
            String estadoPago,
            boolean inscripcionFinalizada,
            boolean puedeReintentarPago,
            String curso,
            LocalDate fechaInicio
    ) {}

    /** Curso disponible para el selector del formulario. */
    public record OfertaPublicaDTO(
            Integer id,
            String nombre,
            String turno,
            String horario,
            LocalDate fechaInicio,
            LocalDate fechaLimite,
            BigDecimal precio
    ) {}
}