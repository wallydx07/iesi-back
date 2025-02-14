package com.example.iesiback.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.example.iesiback.models.IUser;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "users")
// Indica a Jackson que use el campo "username" para identificar la instancia y romper ciclos
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "username")
public class User implements IUser {

    @Id
    @Column(name = "username")  // Mapea el atributo
    private String username;

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

    @NotNull
    @Column(name = "user_status")
    private Boolean userStatus;
    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean admin;

    @NotBlank
    @Column(name = "password")  // Mapea el atributo
    private String password;


  //  @JsonIdentityInfo(
  //          generator = ObjectIdGenerators.PropertyGenerator.class,
  //          property = "roleId"
  //  )
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "user_role",
            joinColumns = { @JoinColumn(name = "username") },
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            uniqueConstraints = { @UniqueConstraint(columnNames = {"username", "role_id"}) }
    )
    private List<Role> roles;

    public User() {
        this.roles = new ArrayList<>();
    }

    // Getters y setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String userDni) {
        this.username = userDni;
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

    public @NotBlank String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank String userPassword) {
        this.password = userPassword;
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

    public Boolean getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Boolean user_status) {
        this.userStatus = user_status;
    }
}
