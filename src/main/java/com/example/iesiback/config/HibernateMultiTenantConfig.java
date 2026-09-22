package com.example.iesiback.config;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Le pasa a Hibernate las INSTANCIAS de TenantResolver y
 * SchemaMultiTenantConnectionProvider ya armadas por Spring (con sus
 * @Autowired resueltos), en vez de dejar que Hibernate las instancie
 * por reflexion a partir del nombre de clase en application.properties.
 *
 * Sin esto, Hibernate crea SchemaMultiTenantConnectionProvider con "new"
 * y el DataSource @Autowired queda null.
 */
@Configuration
public class HibernateMultiTenantConfig {

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(
            TenantResolver tenantResolver,
            SchemaMultiTenantConnectionProvider connectionProvider) {

        return hibernateProperties -> {
            hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
            hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantResolver);
        };
    }
}