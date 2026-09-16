package com.example.iesiback.exception;

/**
 * Se lanza cuando se intenta modificar o eliminar una Nota cuyo campo
 * "editable" está en false. Se mapea a un HTTP 409 (Conflict) porque el
 * recurso existe y la petición es válida, pero el estado actual del
 * recurso impide la operación.
 */
public class NotaNoEditableException extends RuntimeException {

    public NotaNoEditableException(Long notaId) {
        super("La nota con id " + notaId + " no es editable.");
    }

    public NotaNoEditableException(String mensaje) {
        super(mensaje);
    }
}