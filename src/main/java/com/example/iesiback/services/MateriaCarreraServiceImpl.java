package com.example.iesiback.services;

import com.example.iesiback.dto.ActaCursadaDTO;
import com.example.iesiback.dto.CatedraDTO;
import com.example.iesiback.dto.MateriaDTO;
import com.example.iesiback.entities.Carrera;
import com.example.iesiback.entities.MateriaCarrera;
import com.example.iesiback.repositories.MateriaCarreraRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MateriaCarreraServiceImpl implements MateriaCarreraService {

    @Autowired
    private MateriaCarreraRepository materiaCarreraRepository;

//    private NotaService notaService;

//    public MateriaCarreraServiceImpl(NotaService notaService) {
//        this.notaService = notaService;
//    }


    @Override
    public List<MateriaCarrera> obtenerMateriaCarreras() {
        return materiaCarreraRepository.findAll();
    }

    @Override
    public int obtenerCantidadMateriasPorNivel(String carreraId, String nivel) {
        return materiaCarreraRepository.countMateriasPorNivel(carreraId, nivel) - 1;
    }

//    @Override
//    public MateriaCarrera obtenerMateriaCarrera(String carreraId, String materiaId) {
//        return materiaCarreraRepository.findByCarrera_CarreraIdAndMateria_MateriaId(carreraId, materiaId)
//                .orElseThrow(() -> new RuntimeException("MateriaCarrera no encontrada para carreraId: "
//                        + carreraId + " y materiaId: " + materiaId));
//    }


    @Override
    public List<MateriaCarrera> obtenerMateriaCarrera(String carreraId, String materiaId) {

        List<MateriaCarrera> lista = materiaCarreraRepository
                .findByCarrera_CarreraIdAndMateria_MateriaId(carreraId, materiaId);

        if (lista.isEmpty()) {
            throw new RuntimeException(
                    "MateriaCarrera no encontrada [carreraId=" + carreraId + ", materiaId=" + materiaId + "]"
            );
        }
        return lista;
    }


    @Override
    public List<MateriaCarrera> obtenerMateriaCarreraDivision(String carreraId, String materiaId, String division) {

        List<MateriaCarrera> lista = materiaCarreraRepository
                .findByCarrera_CarreraIdAndMateria_MateriaIdAndDivision(carreraId, materiaId, division);

        if (lista.isEmpty()) {
            throw new RuntimeException(
                    "MateriaCarrera no encontrada [carreraId=" + carreraId + ", materiaId=" + materiaId + "]"
            );
        }
        return lista;
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
    public List<CatedraDTO> obtenerCatedrasPorDocenteYAnio(String dni, Integer year) {
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
        Optional<MateriaCarrera> existing = materiaCarreraRepository.findById(Long.valueOf(id));
        if (existing.isPresent()) {
            materiaCarrera.setId(id); // Asegurarse que el id sea Long
            // Corregir la comparación de la firma con != y paréntesis
//            if (!materiaCarrera.getFirma().equals(existing.get().getFirma())) {
//                notaService.permitirEdicionMateria(
//                        existing.get().getCarrera().getCarreraId(),
//                        existing.get().getMateria().getMateriaId(),
//                        materiaCarrera.getFirma()
//                );
//            }

            return materiaCarreraRepository.save(materiaCarrera);
        } else {
            throw new EntityNotFoundException("MateriaCarrera con id " + id + " no encontrada");
        }
    }

    @Override
    public void deleteById(Long id) {
        materiaCarreraRepository.deleteById(id);
    }

    @Override
    public List<Carrera> obtenerCarrerasPorDocente(Long fmcDocente) {
//        return materiaCarreraRepository.findCarrerasByFmcDocente(fmcDocente);
        int anioActual = LocalDate.now().getYear();
        return materiaCarreraRepository.findCarrerasDictadasEsteAnio(fmcDocente, anioActual);

    }

    @Override
    public List<MateriaDTO> obtenerMateriasPorCarreraYDocente(Long fmcDocente, String carreraId) {
//        return materiaCarreraRepository.findMateriasByCarreraAndFmcDocente(fmcDocente, carreraId);
        int anioActual = LocalDate.now().getYear();
        System.out.println("fmcDocente: " + fmcDocente);
        System.out.println("carreraId: " + carreraId);
        System.out.println("anioActual: " + anioActual);
        return materiaCarreraRepository.findMateriasDictadasEsteAnio(fmcDocente, carreraId, anioActual);

    }

    @Override
    public List<ActaCursadaDTO> obtenerActas() {
        return materiaCarreraRepository.obtenerActas();
    }

    @Override
    public List<ActaCursadaDTO> obtenerActasPorAnio(int anio) {
        return materiaCarreraRepository.obtenerActasPorAnio(anio);
    }
//

    @Override
    public MateriaCarrera findMateriaCarreraByFechaAndMateriaOrden(
            LocalDate fecha, String orden, String carreraId) {

        Integer ordenNumero = Integer.valueOf(orden.trim());

        return materiaCarreraRepository
                .buscarMateriaCarreraAnterior(
                        ordenNumero,
                        carreraId,
                        fecha
                )
                .stream()
                .findFirst()
                .orElse(null);
    }

//
//@Override
//public MateriaCarrera findMateriaCarreraByMateriaOrdenCarreraId(
//        String orden, String carreraId) {
//
//    Integer ordenNumero = Integer.valueOf(orden.trim());
//
//    return materiaCarreraRepository
//            .buscarMateriaCarreraOrden(
//                    ordenNumero,
//                    carreraId )
//            .stream()
//            .findFirst()
//            .orElse(null);
//}
//



@Override
public LocalDate obtenerFechaVigencia(String carreraId, String ordenStr) {

        if (ordenStr == null || ordenStr.isBlank()) {
            return null;
        }

        Integer orden = Integer.valueOf(ordenStr.trim());

        return materiaCarreraRepository
                .findFechaByCarreraIdAndOrden(carreraId, orden);
    }
}
