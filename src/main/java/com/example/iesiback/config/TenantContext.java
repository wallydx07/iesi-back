package com.example.iesiback.config;

public class TenantContext {
    private static final ThreadLocal<String> CURRENT_TENANT = ThreadLocal.withInitial(() -> "public");

    public static void setCurrentTenant(String tenant) {
        CURRENT_TENANT.set(tenant);
    }

    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove(); // importante: evita leaks entre requests en el mismo hilo del pool
    }
}