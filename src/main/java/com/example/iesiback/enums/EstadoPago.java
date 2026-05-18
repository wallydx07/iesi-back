package com.example.iesiback.enums;

/**
 * Representa los estados posibles de un pago y define las reglas
 * estrictas de la máquina de estados para sus transiciones.
 */
public enum EstadoPago {
    PENDIENTE,
    APROBADO,
    RECHAZADO,
    CANCELADO;

    /**
     * Valida si un pago puede cambiar desde el estado actual (this) hacia un nuevo estado.
     *
     * @param nuevoEstado El estado al que se intenta cambiar.
     * @return true si la transición está permitida por las reglas de negocio, false en caso contrario.
     */
    public boolean puedeTransicionarA(EstadoPago nuevoEstado) {
        // Regla básica: Permanecer en el mismo estado actual siempre es válido (no hay cambios)
        if (this == nuevoEstado) return true;

        return switch (this) {

            // ⏳ UN PAGO PENDIENTE:
            // Es el estado inicial. Desde aquí se puede tomar cualquier acción:
            // - APROBADO: Si el operador valida que el dinero ingresó correctamente.
            // - RECHAZADO: Si hay un error en los datos o el comprobante adjunto.
            // - CANCELADO: Si el usuario desiste de realizar el pago antes de ser evaluado.
            case PENDIENTE ->
                    nuevoEstado == APROBADO ||
                            nuevoEstado == RECHAZADO ||
                            nuevoEstado == CANCELADO;

            // 🟢 UN PAGO APROBADO:
            // El dinero ya impactó en el sistema. Por seguridad y auditoría,
            // NO puede volver a 'Pendiente' ni a 'Rechazado'.
            // - Única salida: CANCELADO (En caso de que requiera una anulación total/devolución).
            case APROBADO ->
                    nuevoEstado == CANCELADO;

            // 🟡 UN PAGO RECHAZADO:
            // El operador detectó un problema (ej. comprobante borroso).
            // - Única salida: PENDIENTE. Se regresa a pendiente para que el usuario o el
            //   sistema puedan corregir los datos del pago y permitir una nueva evaluación.
            case RECHAZADO ->
                    nuevoEstado == PENDIENTE;

            // ⚫ UN PAGO CANCELADO:
            // Estado final absoluto (Anulado). Representa un punto sin retorno.
            // No se permite reactivarlo, modificarlo ni auditarlo bajo ninguna circunstancia
            // para evitar fraudes o inconsistencias de caja.
            case CANCELADO ->
                    false;
        };
    }
}