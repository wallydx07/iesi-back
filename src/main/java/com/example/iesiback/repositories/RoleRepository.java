package com.example.iesiback.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.iesiback.entities.Role;

public interface RoleRepository extends CrudRepository<Role, Long>{

    Optional<Role> findByName(String name);

}
