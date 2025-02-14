package com.example.iesiback.repositories;

import java.util.Optional;

import com.example.iesiback.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.example.iesiback.entities.Role;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long>{

    Optional<Role> findByRoleNombre(String name);



}
