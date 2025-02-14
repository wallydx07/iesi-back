package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="role")
public class Role {
    @Id
   // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")  // Mapea el atributo id a la columna role_id en la base de datos
    private long roleId;

    @Column(name = "role_nombre")  // Mapea el atributo name a la columna role_nombre en la base de datos
    private String roleNombre;

    @ManyToMany(mappedBy = "roles")
    @JsonBackReference
    private List<User> users = new ArrayList<>();

    public Role() {

    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleNombre() {
        return roleNombre;
    }

    public void setRoleNombre(String roleNombre) {
        this.roleNombre = roleNombre;
    }
}