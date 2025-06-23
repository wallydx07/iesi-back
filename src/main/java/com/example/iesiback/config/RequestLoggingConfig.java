package com.example.iesiback.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class RequestLoggingConfig {

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeClientInfo(true);  // Registrar la información del cliente
        filter.setIncludeQueryString(true); // Registrar la consulta de la URL
        filter.setIncludePayload(true);     // Registrar el cuerpo de la solicitud
        filter.setMaxPayloadLength(10000);  // Limitar la longitud máxima del cuerpo registrado
        filter.setAfterMessagePrefix("Request: "); // Prefijo para los logs de solicitud
        return filter;
    }
}