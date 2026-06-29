package com.example.iesiback.enums;

/**
 * Representa los estados posibles de un pago y define las reglas
 * estrictas de la máquina de estados para sus transiciones.
 */
public enum EstadoPago {
    PENDIENTE,
    APROBADO,
    RECHAZADO,
    CANCELADO,
    REEMBOLSADO,
    CONTRACARGO;

    /**
     * Valida si un pago puede cambiar desde el estado actual (this)
     * hacia un nuevo estado.
     *
     * @param nuevoEstado El estado al que se intenta cambiar.
     * @return true si la transición está permitida, false en caso contrario.
     */
    public boolean puedeTransicionarA(EstadoPago nuevoEstado) {

        // Permanecer en el mismo estado siempre es válido.
        if (this == nuevoEstado) return true;

        return switch (this) {

            // Estado inicial.
            case PENDIENTE ->
                    nuevoEstado == APROBADO ||
                            nuevoEstado == RECHAZADO ||
                            nuevoEstado == CANCELADO;

            // Pago acreditado.
            case APROBADO ->
                    nuevoEstado == REEMBOLSADO ||
                            nuevoEstado == CONTRACARGO ||
                            nuevoEstado == CANCELADO;

            // Puede corregirse y volver a revisión.
            case RECHAZADO ->
                    nuevoEstado == PENDIENTE;

            // Un pago reembolsado puede posteriormente recibir un contracargo
            // únicamente si el negocio lo permite.
            case REEMBOLSADO ->
                    nuevoEstado == CONTRACARGO;

            // Estados finales.
            case CANCELADO,
                 CONTRACARGO ->
                    false;
        };
    }
}