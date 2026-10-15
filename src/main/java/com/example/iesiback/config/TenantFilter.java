package com.example.iesiback.config;

import java.io.IOException;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Component
public class TenantFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TenantFilter.class);

    private static final String TENANT_HEADER = "X-Tenant";
    private static final String TENANT_OFICIAL = "oficial";
    private static final String TENANT_BORRADOR = "public";
    private static final Set<String> ROLES_CARGA_OFICIAL = Set.of(
            "ROLE_TITULACION",
            "ROLE_ADMIN",
            "ROLE_DIRECTIVO"// agregá acá los que necesites
    );
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
                    log.warn("Acceso denegado a tenant oficial: {} {} auth={}",
                            request.getMethod(), request.getRequestURI(),
                            auth == null ? "null" : auth.getAuthorities());
                    throw new AccessDeniedException(
                            "No tenes permiso para trabajar sobre notas oficiales");
                }
                tenant = TENANT_OFICIAL;
            }

            log.info("{} {} | X-Tenant={} → {} | hilo={}",
                    request.getMethod(), request.getRequestURI(),
                    tenantSolicitado, tenant, Thread.currentThread().getName());

            TenantContext.setCurrentTenant(tenant);
            filterChain.doFilter(request, response);

        } finally {
            TenantContext.clear();
        }
    }

    private boolean tieneRolCargaOficial(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(ROLES_CARGA_OFICIAL::contains);
    }
}