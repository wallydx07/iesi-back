package com.example.iesiback.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.iesiback.entities.User;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long>{

    Page<User> findAll(Pageable pageable);
    Optional<User> findByUsername(String username);
    Optional<User> findByUserEmail(String email); // 👈 Agregado
//    List<User> findByRolesIn(List<String> roles);
@Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleNombre IN :nombres")
List<User> findByRolesNombreIn(@Param("nombres") List<String> nombres);



}
