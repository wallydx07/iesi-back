package com.example.iesiback.services;

import com.example.iesiback.entities.Atencion;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AtencionService {
    List<Atencion> findAll();
    List<Atencion> findAllByOrderByAtencionFechaDesc();
    Optional<Atencion> findById(Integer id);
    Optional<Atencion> findByCodigoSeguimiento(String codigo);
    Atencion save(Atencion atencion);
    Atencion update(Atencion atencion);
    void deleteById(Integer id);
    List<Atencion> findByDni(Long dni);
    List<Atencion> findByApellidoNombre(String apellidoNombre);
    List<Atencion> findByCorreo(String correo);
    List<Atencion> findByDestino(String destino);
    List<Atencion> findByUsuario(String usuario);
    List<Atencion> findByResuelto(String resuelto);
    List<Atencion> findByFecha(LocalDate fecha);
    String getSecuenciaPorTipo(String tipo);
    List<Atencion> obtenerPorGestor(Long gestorDni);
    List<Atencion> findByAtencionReferencia(Integer referencia);

    List<Atencion> findByAtencionLegajoId(String legajoId);

}