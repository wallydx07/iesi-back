package com.example.iesiback.services;

import com.example.iesiback.entities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    @Autowired
    private com.example.iesiback.repositories.RoleRepository RoleRepository;

    public List<Role> obtenerRoles() {
        return (List<Role>) RoleRepository.findAll();
    }

}