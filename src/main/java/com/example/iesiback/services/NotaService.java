package com.example.iesiback.services;

import com.example.iesiback.dto.NotaMateriaDTO;
import com.example.iesiback.entities.Nota;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface NotaService {
    List<Nota> obtenerNotas();
    List<Nota> obtenerNotasPorLegajo(String legajoId);
    List<NotaMateriaDTO> obtenerTodasNotasPorLegajo(String legajoId);
    List<NotaMateriaDTO> obtenerNotasNoAprobadasPorLegajo(String legajoId); // ✅ Corregido, sin implementación en la interfaz
}
