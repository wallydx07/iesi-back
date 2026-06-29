package com.example.iesiback.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Maneja errores de negocio personalizados
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {

        log.warn(
                "Error de negocio - {} {} - {}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage()
        );

        ApiError errorDetails = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Error de Negocio",
                ex.getMessage()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // Maneja cualquier otro error inesperado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {

        log.error(
                "Error inesperado - {} {}",
                request.getMethod(),
                request.getRequestURI(),
                ex
        );

        ApiError errorDetails = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error Interno del Servidor",
                "Ocurrió un error inesperado en el sistema."
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}