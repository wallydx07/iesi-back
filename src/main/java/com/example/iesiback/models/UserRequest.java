package com.example.iesiback.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class UserRequest implements IUser {

    @NotBlank
    private String userNombre;

    @NotBlank
    private String userApellido;

    @NotEmpty
    @Email
    private String userEmail;

    @NotBlank
    private String username;

    private boolean admin;

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

    public @NotBlank String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank String username) {
        this.username = username;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    
}
