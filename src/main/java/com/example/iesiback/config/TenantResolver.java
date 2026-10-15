package com.example.iesiback.config;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TenantResolver implements CurrentTenantIdentifierResolver<String> {

    private static final Logger log = LoggerFactory.getLogger(TenantResolver.class);

//    @Override
//    public String resolveCurrentTenantIdentifier() {
//        return TenantContext.getCurrentTenant();
//    }

    @Override
    public String resolveCurrentTenantIdentifier() {
        String t = TenantContext.getCurrentTenant();
        if (t == null) {
            log.warn("Tenant NO seteado en hilo {} → uso public", Thread.currentThread().getName(),
                    new Exception("origen")); // te imprime el stack de quién abrió la sesión
            return "public";
        }
        return t;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}