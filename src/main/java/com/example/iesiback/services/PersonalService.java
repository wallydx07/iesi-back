package com.example.iesiback.services;

import com.example.iesiback.entities.Personal;

import java.util.List;
import java.util.Optional;

public interface PersonalService {

    List<Personal> findAll();

    Optional<Personal> findById(String id);

    Personal save(Personal personal);

    void deleteById(String id);

    boolean existsByDni(String dni);

    // Métodos de búsqueda por atributo
    List<Personal> findByPersonalNombre(String personalNombre);
    List<Personal> findByPersonalApellido(String personalApellido);
    List<Personal> findByPersonalCorreo(String personalCorreo);
    List<Personal> findByPersonalCelular(String personalCelular);
    List<Personal> findByPersonalTipo(String personalTipo);
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
