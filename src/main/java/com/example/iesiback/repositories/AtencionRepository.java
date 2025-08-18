package com.example.iesiback.repositories;

import com.example.iesiback.entities.Atencion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AtencionRepository extends JpaRepository<Atencion, Integer> {
    List<Atencion> findByAtencionDni(Long dni);
    List<Atencion> findByAtencionApellidoNombreContainingIgnoreCase(String apellidoNombre);
    List<Atencion> findByAtencionCorreoContainingIgnoreCase(String correo);
    List<Atencion> findByAtencionDestino(String destino);
    List<Atencion> findByAtencionUsuario(String usuario);
    List<Atencion> findByAtencionResuelto(Boolean resuelto);
    List<Atencion> findByAtencionFecha(LocalDate fecha);
    Optional<Atencion> findByCodigoSeguimiento(String codigoSeguimiento);
    List<Atencion> findAllByOrderByIdDesc();

}