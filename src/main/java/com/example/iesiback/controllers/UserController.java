package com.example.iesiback.controllers;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.iesiback.entities.User;
import com.example.iesiback.models.UserRequest;
import com.example.iesiback.services.UserService;

import jakarta.validation.Valid;


@CrossOrigin(origins={"*"})
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService service;
    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> list() {
        return service.findAll();
    }

    @GetMapping("/page/{page}")
    public Page<User> listPageable(@PathVariable Integer page) {
        Pageable pageable = PageRequest.of(page, 4);
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Optional<User> userOptional = service.findById(id);
        if (userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(userOptional.orElseThrow());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("error", "el usuario no se encontro por el id:" + id));
    }
    
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(user));
    }



    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody UserRequest user, BindingResult result, @PathVariable Long id) {

        if (result.hasErrors()) {
            return validation(result);
        }
        
        Optional<User> userOptional = service.update(user, id);

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<User> userOptional = service.findById(id);
        if (userOptional.isPresent()) {
            service.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> {
            errors.put(error.getField(), "El campo " + error.getField() + " " + error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        return service.getAuthenticatedUser()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }




    @PutMapping("/{username}")
    public ResponseEntity<User> updateUser(@PathVariable String username, @RequestBody User user) {
        User updated = service.update(username, user);
        if (updated != null) return ResponseEntity.ok(updated);
        else return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long username) {
        service.deleteByUsername(username);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    @PostMapping("/{username}/reset-password")
    public ResponseEntity<String> resetPassword(
            @PathVariable String username,
            @RequestBody String newPassword) {

        boolean success = service.resetPassword(username, newPassword);

        if (success) {
            return ResponseEntity.ok("Contraseña actualizada.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<String> requestPasswordReset(@RequestBody String email) {
        service.sendPasswordResetToken(email);
        return ResponseEntity.ok("Si el correo está registrado, se ha enviado un enlace.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPasswordWithToken(
            @RequestParam String token,
            @RequestBody String newPassword) {
        boolean success = service.resetPasswordWithToken(token, newPassword);
        if (success) return ResponseEntity.ok("Contraseña actualizada.");
        return ResponseEntity.badRequest().body("Token inválido o expirado.");
    }

    @GetMapping("/personal")
    public ResponseEntity<List<User>> getPersonalUsers() {
        List<String> roles = List.of("ROLE_PERSONAL", "ROLE_ADMIN", "ROLE_TUTOR");
        List<User> personalUsers = service.getUsuariosPorRoles(roles);
        if (personalUsers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(personalUsers);
    }
}
