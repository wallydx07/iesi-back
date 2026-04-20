package com.example.iesiback.repositories;

import com.example.iesiback.entities.Tramite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TramiteRepository extends JpaRepository<Tramite, Integer> {
    List<Tramite> findByTramiteDni(Long dni);
    List<Tramite> findByTramiteApellidoNombreContainingIgnoreCase(String apellidoNombre);
    List<Tramite> findByTramiteCorreoContainingIgnoreCase(String correo);
    List<Tramite> findByTramiteDestino(String destino);
    List<Tramite> findByTramiteUsuario(String usuario);
    List<Tramite> findByTramiteEstado(String resuelto);
    List<Tramite> findByTramiteFecha(LocalDate fecha);
    Optional<Tramite> findByCodigoSeguimiento(String codigoSeguimiento);
    List<Tramite> findAllByOrderByIdDesc();
    List<Tramite> findByGestorDni(Long gestorDni);
    List<Tramite> findByTramiteReferencia(Integer atencionReferencia);
    List<Tramite> findAllByOrderByTramiteFechaDescNumeroTipoDesc();
    List<Tramite> findByLegajoId(String atencionLegajo);



}