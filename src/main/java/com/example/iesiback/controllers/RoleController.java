package com.example.iesiback.controllers;

import com.example.iesiback.entities.Role;
import com.example.iesiback.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public List<Role> obtenerRoles() {
        return roleService.obtenerRoles();
    }
}
