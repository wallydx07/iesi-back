package com.example.iesiback.repositories;

import com.example.iesiback.entities.Personal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalRepository extends JpaRepository<Personal, String> {
    boolean existsById(String id);
    List<Personal> findByPersonalNombreContainingIgnoreCase(String personalNombre);
    List<Personal> findByPersonalApellidoContainingIgnoreCase(String personalApellido);
    List<Personal> findByPersonalCorreoContainingIgnoreCase(String personalCorreo);
    List<Personal> findByPersonalCelularContainingIgnoreCase(String personalCelular);
    List<Personal> findByPersonalTipoContainingIgnoreCase(String personalTipo);
    List<Personal> findByPersonalRdni(Boolean personalRdni);
    List<Personal> findByPersonalRresidencia(Boolean personalRresidencia);
    List<Personal> findByPersonalRplanilla(Boolean personalRplanilla);
    List<Personal> findByPersonalRsanitario(Boolean personalRsanitario);
    List<Personal> findByPersonalRnacimiento(Boolean personalRnacimiento);
    List<Personal> findByDeclaracion(Boolean declaracion);
    List<Personal> findByCargo(Boolean cargo);
    List<Personal> findByCuil(Boolean cuil);
    List<Personal> findByTitulo(Boolean titulo);
    List<Personal> findByCurriculum(Boolean curriculum);
}
