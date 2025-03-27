package com.example.iesiback.services;

import com.example.iesiback.dto.AporteDTO;
import com.example.iesiback.entities.Aporte;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AporteService {
     PDDocument generarReciboPDF(String libreta, String turno);
     Aporte crearAporte(Aporte aporte);
     List<AporteDTO> getAportesConDatos();
     Aporte save(Aporte aporte);
     Aporte findAporteById(String id);

     List<Aporte> obtenerAportesPorLegajoId(String legajoId);
}