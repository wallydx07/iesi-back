package com.example.iesiback.entities;

import jakarta.persistence.*;

@Entity
@Table(name="role")
public class Role {
    @Id
   // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")  // Mapea el atributo id a la columna role_id en la base de datos
    private long roleId;

    @Column(name = "role_nombre")  // Mapea el atributo name a la columna role_nombre en la base de datos
    private String roleNombre;

    public Role() {

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