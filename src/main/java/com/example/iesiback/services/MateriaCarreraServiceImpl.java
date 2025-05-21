package com.example.iesiback.services;

import com.example.iesiback.dto.CatedraDTO;
import com.example.iesiback.dto.MateriaCarreraDTO;
import com.example.iesiback.entities.MateriaCarrera;
import com.example.iesiback.repositories.MateriaCarreraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MateriaCarreraServiceImpl implements MateriaCarreraService {

    @Autowired
    private MateriaCarreraRepository materiaCarreraRepository;

    @Override
    public List<MateriaCarrera> obtenerMateriaCarreras() {
        return materiaCarreraRepository.findAll();
    }

@Override
public int obtenerCantidadMateriasPorNivel(String carreraId, String nivel) {
        return materiaCarreraRepository.countMateriasPorNivel(carreraId, nivel) - 1;
    }

    @Override
    public MateriaCarrera obtenerMateriaCarrera(String carreraId, String materiaId) {
        return materiaCarreraRepository.findByCarrera_CarreraIdAndMateria_MateriaId(carreraId, materiaId)
                .orElseThrow(() -> new RuntimeException("MateriaCarrera no encontrada para carreraId: "
                        + carreraId + " y materiaId: " + materiaId));
    }

    @Override
    public List<MateriaCarrera> obtenerMateriasPorCarrera(String carreraId) {
        return materiaCarreraRepository.findByCarrera_CarreraId(carreraId);
    }
    // 🔹 Nuevo método para buscar por ID
    public Optional<MateriaCarrera> obtenerMateriaCarreraPorId(Long id) {
        return materiaCarreraRepository.findById(id);
    }


    @Override
    public List<CatedraDTO> obtenerCatedrasPorDocenteYAnio(String dni, String year) {
        return materiaCarreraRepository.findCatedrasByDocenteAndYear(dni, year);
    }
/*

@Override
public int actualizarMateriaCarrera(Long id, MateriaCarreraDTO materiaCarreraDTO) {
        return materiaCarreraRepository.actualizarMateriaCarrera(
                id,
                materiaCarreraDTO.getLibro(),
                materiaCarreraDTO.getFolio(),
                materiaCarreraDTO.getFecha(),
                materiaCarreraDTO.isFirma(),
                materiaCarreraDTO.getFmcDocente(),
                materiaCarreraDTO.getDivision(),
                materiaCarreraDTO.getTurno(),
                materiaCarreraDTO.getDia(),
                materiaCarreraDTO.getInicio(),
                materiaCarreraDTO.getFin()
        );
    }*/

    @Override
    public List<MateriaCarrera> findAll() {
        return materiaCarreraRepository.findAll();
    }

    @Override
    public Optional<MateriaCarrera> findById(Long id) {
        return materiaCarreraRepository.findById(id);
    }

    @Override
    public MateriaCarrera save(MateriaCarrera materiaCarrera) {
        return materiaCarreraRepository.save(materiaCarrera);
    }

    @Override
    public MateriaCarrera update(Integer id, MateriaCarrera materiaCarrera) {
        materiaCarrera.setId(id);
        return materiaCarreraRepository.save(materiaCarrera);
    }

    @Override
    public void deleteById(Long id) {
        materiaCarreraRepository.deleteById(id);
    }
}
