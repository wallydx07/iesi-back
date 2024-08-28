package com.example.iesiback.entities;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.*;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.iesiback.models.IUser;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User implements IUser {

    @Id
    @Column(name = "user_dni")  // Mapea el atributo
    private String userDni;

    @NotBlank
    @Column(name = "user_nombre")  // Mapea el atributo
    private String userNombre;

    @NotBlank
    @Column(name = "user_apellido")  // Mapea el atributo
    private String userApellido;

    @NotEmpty
    @Email
    @Column(name = "user_email")  // Mapea el atributo
    private String userEmail;


    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean admin;

    @NotBlank
    @Column(name = "user_password")  // Mapea el atributo
    private String userPassword;

    @JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="user_role",
        joinColumns = {@JoinColumn(name="user_dni")},
        inverseJoinColumns = @JoinColumn(name="role_id"),
        uniqueConstraints = { @UniqueConstraint(columnNames = {"user_dni", "role_id"})}
    )
    private List<Role> roles;
    public User() {
        this.roles = new ArrayList<>();
    }


    public String getUserDni() {
        return userDni;
    }

    public void setUserDni(String userDni) {
        this.userDni = userDni;
    }

    public @NotBlank String getUserNombre() {
        return userNombre;
    }

    public void setUserNombre(@NotBlank String userNombre) {
        this.userNombre = userNombre;
    }

    public @NotBlank String getUserApellido() {
        return userApellido;
    }

    public void setUserApellido(@NotBlank String userApellido) {
        this.userApellido = userApellido;
    }

    public @NotEmpty @Email String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(@NotEmpty @Email String userEmail) {
        this.userEmail = userEmail;
    }

    public @NotBlank String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(@NotBlank String userPassword) {
        this.userPassword = userPassword;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    
}
