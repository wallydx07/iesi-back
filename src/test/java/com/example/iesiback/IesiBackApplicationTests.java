package com.example.iesiback;

import com.example.iesiback.services.CertificadoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class NotaServiceTest {

    @Autowired
    private CertificadoService certificadoService;

    @Test
    void testGeneraTroquelNota_ManualAtencion() {

    }
}
