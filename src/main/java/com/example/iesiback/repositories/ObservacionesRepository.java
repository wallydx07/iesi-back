    package com.example.iesiback.repositories;

    import com.example.iesiback.entities.Observacione;
    import org.springframework.data.jpa.repository.JpaRepository;

    import java.util.List;
    import java.util.Optional;

    public interface ObservacionesRepository extends JpaRepository<Observacione, String> {
      //  List<Observacione> findByLegajoId(String legajoId);
        // Buscar por legajo_id en la entidad Legajo
        List<Observacione> findByLegajo_LegajoId(String legajoId);
        // Opción 1: Usando Query derivada
        Observacione findTopByLegajo_LegajoIdOrderByFechaDesc(String legajoId);


    }

