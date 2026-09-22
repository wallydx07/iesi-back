package com.example.iesiback.config;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro que resuelve el esquema (tenant) activo para el request actual,
 * a partir del header X-Tenant enviado por el frontend.
 *
 * - Si no viene el header, o viene distinto de "oficial", se usa "public"
 *   (el esquema actual / borrador), sin requerir ningun rol especial.
 * - Si viene X-Tenant: oficial, se valida que el usuario autenticado tenga
 *   el rol ROLE_TITULACION antes de aceptar el tenant "oficial".
 *
 * Debe registrarse DESPUES de JwtValidationFilter en la SecurityFilterChain,
 * para que SecurityContextHolder ya tenga la Authentication resuelta.
 */
@Component
public class TenantFilter extends OncePerRequestFilter {

    private static final String TENANT_HEADER = "X-Tenant";
    private static final String TENANT_OFICIAL = "oficial";
    private static final String TENANT_BORRADOR = "public";
    private static final String ROL_CARGA_OFICIAL = "ROLE_TITULACION";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String tenantSolicitado = request.getHeader(TENANT_HEADER);
            String tenant = TENANT_BORRADOR;

            if (TENANT_OFICIAL.equalsIgnoreCase(tenantSolicitado)) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();

                if (auth == null || !auth.isAuthenticated() || !tieneRolCargaOficial(auth)) {
                    throw new AccessDeniedException(
                            "No tenes permiso para trabajar sobre notas oficiales");
                }
                tenant = TENANT_OFICIAL;
            }

            TenantContext.setCurrentTenant(tenant);
            filterChain.doFilter(request, response);

        } finally {
            // Siempre limpiar, incluso si hubo excepcion, para no filtrar
            // el tenant entre requests que reusan el mismo hilo del pool.
            TenantContext.clear();
        }
    }

    private boolean tieneRolCargaOficial(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(ROL_CARGA_OFICIAL::equals);
    }
}