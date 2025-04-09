package com.example.iesiback.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.example.iesiback.entities.PasswordResetToken;
import com.example.iesiback.repositories.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.iesiback.entities.Role;
import com.example.iesiback.entities.User;
import com.example.iesiback.models.UserRequest;
import com.example.iesiback.repositories.RoleRepository;
import com.example.iesiback.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findById(@NonNull Long id) {
        return repository.findById(id);
    }

    @Transactional
    @Override
    public User save(User user) {
        user.setRoles(getRolesFromRequest(user)); // Asignar roles desde el JSON
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    @Override
    @Transactional
    public Optional<User> update(UserRequest userRequest, Long id) {
        Optional<User> userOptional = repository.findById(id);

        if (userOptional.isPresent()) {
            User userDb = userOptional.get();
            userDb.setUserEmail(userRequest.getUserEmail());
            userDb.setUserApellido(userRequest.getUserApellido());
            userDb.setUserNombre(userRequest.getUserNombre());
            userDb.setUsername(userRequest.getUsername());
            userDb.setRoles(getRolesFromRequest(userDb)); // Corregido para usar User en lugar de UserRequest
            return Optional.of(repository.save(userDb));
        }
        return Optional.empty();
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private List<Role> getRolesFromRequest(User user) {
        List<Role> roles = new ArrayList<>();

        // Asignar roles desde el JSON si existen
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            for (Role role : user.getRoles()) {
                if (role.getRoleId() != null) {
                    Role roleFromDb = roleRepository.findById(role.getRoleId())
                            .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + role.getRoleId()));
                    roles.add(roleFromDb);
                } else {
                    throw new IllegalArgumentException("Cada rol debe tener un 'roleId' válido.");
                }
            }
        }

        // Agregar ROLE_USER por defecto si no está presente
        Optional<Role> defaultRole = roleRepository.findByRoleNombre("ROLE_USER");
        defaultRole.ifPresent(role -> {
            if (roles.stream().noneMatch(r -> r.getRoleNombre().equals("ROLE_USER"))) {
                roles.add(role);
            }
        });

        // Si es admin, agregar ROLE_ADMIN
        if (user.isAdmin()) {
            Optional<Role> adminRole = roleRepository.findByRoleNombre("ROLE_ADMIN");
            adminRole.ifPresent(roles::add);
        }

        return roles;
    }

    @Transactional(readOnly = true)
    public Optional<User> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        String username = authentication.getName(); // Obtiene el username del token

        return repository.findByUsername(username);
    }

    @Override
    public User update(String username, User updatedUser) {
        return repository.findByUsername(username).map(user -> {
            user.setUserNombre(updatedUser.getUserNombre());
            user.setUserApellido(updatedUser.getUserApellido());
            user.setUserEmail(updatedUser.getUserEmail());
            user.setUserStatus(updatedUser.getUserStatus());
            // No actualizamos password ni roles aquí por seguridad
            return repository.save(user);
        }).orElse(null);
    }

    @Override
    public void deleteByUsername(Long username) {
        repository.deleteById(username);
    }

    @Override
    public boolean resetPassword(String username, String newPassword) {
        return repository.findByUsername(username).map(user -> {
            user.setPassword(newPassword);
            repository.save(user);
            return true;
        }).orElse(false);
    }



    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender; // Necesitás configurar esto

    @Override
    public void sendPasswordResetToken(String email) {
        System.out.println("🔵 Solicitud de reset recibida para: " + email);

        Optional<User> userOpt = repository.findByUserEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("✅ Usuario encontrado: " + user.getUsername());

            PasswordResetToken token = new PasswordResetToken(user);
            tokenRepository.save(token);

            String resetLink = "http://localhost:4200/reset-password?token=" + token.getToken();
            System.out.println("🔗 Enlace generado: " + resetLink);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getUserEmail());
            message.setSubject("Restablecer tu contraseña");
            message.setText("Hacé clic en el siguiente enlace para restablecer tu contraseña: " + resetLink);

            try {
                mailSender.send(message);
                System.out.println("📧 Correo enviado correctamente a: " + user.getUserEmail());
            } catch (Exception e) {
                System.err.println("❌ Error al enviar el correo: " + e.getMessage());
                e.printStackTrace();
            }

        } else {
            System.err.println("⚠️ No se encontró usuario con email: " + email);
        }
    }


    @Override
    public boolean resetPasswordWithToken(String token, String newPassword) {
        System.out.println("🔐 Intentando restablecer contraseña con token: " + token);

        try {
            Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

            if (tokenOpt.isPresent()) {
                PasswordResetToken prt = tokenOpt.get();
                System.out.println("✅ Token encontrado para el usuario: " + prt.getUser().getUsername());

                if (prt.getExpirationDate().isAfter(LocalDateTime.now())) {
                    User user = prt.getUser();
                    System.out.println("🕒 Token válido. Procediendo a actualizar contraseña...");
                    user.setPassword(newPassword); // 🚨 Asegurate de cifrarla en producción

                    user.setPassword(passwordEncoder.encode(newPassword));
                    repository.save(user);
                    tokenRepository.delete(prt);
                    System.out.println("✅ Contraseña actualizada correctamente y token eliminado.");
                    return true;

                } else {
                    System.err.println("⛔ Token expirado: " + prt.getExpirationDate());
                }

            } else {
                System.err.println("❌ Token no encontrado en la base de datos.");
            }

        } catch (Exception e) {
            System.err.println("🚨 Error al restablecer la contraseña: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }



}
