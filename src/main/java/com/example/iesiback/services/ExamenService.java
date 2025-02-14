package com.example.iesiback.services;

import com.example.iesiback.dto.InscripcionExamenDTO;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ExamenService {

    List<InscripcionExamenDTO> completarCursadas(String legajoId, String turno);
}
