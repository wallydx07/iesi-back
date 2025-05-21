package com.example.iesiback.services;

import com.example.iesiback.dto.CatedraDTO;
import com.example.iesiback.dto.MateriaCarreraDTO;
import com.example.iesiback.entities.MateriaCarrera;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface MateriaCarreraService {
    List<MateriaCarrera> obtenerMateriaCarreras();
    int obtenerCantidadMateriasPorNivel(String carreraId, String nivel);
    MateriaCarrera obtenerMateriaCarrera(String carreraId, String materiaId);
    List<MateriaCarrera> obtenerMateriasPorCarrera(String carreraId);
    Optional<MateriaCarrera> obtenerMateriaCarreraPorId(Long id);
    List<CatedraDTO> obtenerCatedrasPorDocenteYAnio(String dni, String year);
//    int actualizarMateriaCarrera(Long id, MateriaCarreraDTO materiaCarreraDTO);



    List<MateriaCarrera> findAll();
    Optional<MateriaCarrera> findById(Long id);
    MateriaCarrera save(MateriaCarrera materiaCarrera);
    MateriaCarrera update(Integer id, MateriaCarrera materiaCarrera);
    void deleteById(Long id);
}
