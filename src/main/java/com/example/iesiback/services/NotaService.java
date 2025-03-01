package com.example.iesiback.services;

import com.example.iesiback.dto.NotaExamenDTO;
import com.example.iesiback.dto.NotaCursadaDTO;
import com.example.iesiback.dto.NotaMateriaDTO;
import com.example.iesiback.entities.Nota;
import com.example.iesiback.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface NotaService {
    List<Nota> obtenerNotas();
    List<NotaMateriaDTO> obtenerTodasNotasPorLegajo(String legajoId);
    List<NotaMateriaDTO> obtenerNotasNoAprobadasPorLegajo(String legajoId); // ✅ Corregido, sin implementación en la interfaz
    List<NotaCursadaDTO> findNotasByCarreraAndMateria(String carreraId, String materiaId, boolean cursadaInscripto);
    List<NotaExamenDTO> findExamenesByCursadaExamenIdMateriaCarrera(Long  cursadaExamenId, Boolean soloInscritos);
    boolean isMateriaAprobada(String legajoId, String materiaId);
    Nota guardarNota(Nota nota);
    Nota obtenerNotaPorId(Long notaId);

    Nota actualizarNota(Long id, Nota nota) throws ResourceNotFoundException;
}
