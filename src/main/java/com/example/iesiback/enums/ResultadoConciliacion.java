package com.example.iesiback.enums;

public enum ResultadoConciliacion {
    CONCILIADO,       // existe en ambos lados, estado y monto coinciden
    ESTADO_DISTINTO,  // existe en ambos, pero el estado difiere
    MONTO_DISTINTO,   // existe en ambos, mismo estado, monto difiere
    REEMBOLSADO,      // MP lo devolvió/contracargó y localmente figura APROBADO ⚠️
    SOLO_LOCAL,       // tenemos mpPaymentId pero MP no lo devuelve
    SOLO_MP           // MP lo tiene y nosotros no → webhook perdido ⚠️
}