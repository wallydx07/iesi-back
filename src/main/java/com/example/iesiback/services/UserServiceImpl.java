package com.example.iesiback.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
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
}
