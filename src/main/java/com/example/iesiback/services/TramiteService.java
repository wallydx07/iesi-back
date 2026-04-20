package com.example.iesiback.services;

import com.example.iesiback.entities.Tramite;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TramiteService {
    List<Tramite> findAll();
    List<Tramite> findAllByOrderByAtencionFechaDesc();
    Optional<Tramite> findById(Integer id);
    Optional<Tramite> findByCodigoSeguimiento(String codigo);
    Tramite save(Tramite atencion);
    Tramite update(Tramite atencion);
    void deleteById(Integer id);
    List<Tramite> findByDni(Long dni);
    List<Tramite> findByApellidoNombre(String apellidoNombre);
    List<Tramite> findByCorreo(String correo);
    List<Tramite> findByDestino(String destino);
    List<Tramite> findByUsuario(String usuario);
    List<Tramite> findByResuelto(String resuelto);
    List<Tramite> findByFecha(LocalDate fecha);
    String getSecuenciaPorTipo(String tipo);
    List<Tramite> obtenerPorGestor(Long gestorDni);
    List<Tramite> findByAtencionReferencia(Integer referencia);

    List<Tramite> findByAtencionLegajoId(String legajoId);

}