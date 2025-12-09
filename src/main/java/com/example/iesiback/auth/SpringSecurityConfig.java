package com.example.iesiback.auth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import com.example.iesiback.auth.filter.JwtAuthenticationFilter;
import com.example.iesiback.auth.filter.JwtValidationFilter;

@Configuration
public class SpringSecurityConfig {
    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http.authorizeHttpRequests(authz -> authz
                        .requestMatchers(HttpMethod.GET, "/api/users", "/api/users/page/{page}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/documento/descargar").authenticated() // 👈 ESTA ES CLAVE
                        .requestMatchers("/api/users/request-password-reset", "/api/users/reset-password").permitAll()
                        .requestMatchers("/debug/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/horarios/**").permitAll() // Horarios sin auth
                        .requestMatchers(HttpMethod.POST, "/api/asistencias/desde-dispositivo").permitAll() // Asistencia sin auth
                        .requestMatchers(HttpMethod.GET,"/api/constancia-precios/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/alumnos/buscar/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/legajos/**").permitAll()   // <-- abrir legajos
                        .requestMatchers(HttpMethod.GET, "/api/inscripcion/legajo/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/alumnos/legajo/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/carreras/anio-cursada").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/atenciones/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/certificados").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/constancias/batch").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/pagos/atencion/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/documento/upload").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/atenciones/seguimiento/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/inscripcion/estado-estudiante").permitAll()
                        // 🔥 Necesario para SockJS
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/ws").permitAll()
                        .requestMatchers("/ws/info/**").permitAll()

                        // 🔥 Necesario para STOMP topics
                        .requestMatchers("/topic/**").permitAll()
                        .requestMatchers("/app/**").permitAll()
                        .anyRequest().authenticated())
                .cors(cors -> cors.configurationSource(configurationSource()))
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtValidationFilter(authenticationManager()))
                .csrf(config -> config.disable())
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    CorsConfigurationSource configurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        //config.setAllowedOrigins(Arrays.asList("https://gestionacademica.iesijujuy.edu.ar:8443"));
        config.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<CorsFilter>(
                new CorsFilter(this.configurationSource()));
        corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return corsBean;
    }
}
