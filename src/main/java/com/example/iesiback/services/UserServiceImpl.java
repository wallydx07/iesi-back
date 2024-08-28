package com.example.iesiback.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.iesiback.entities.Role;
import com.example.iesiback.entities.User;
import com.example.iesiback.models.IUser;
import com.example.iesiback.models.UserRequest;
import com.example.iesiback.repositories.RoleRepository;
import com.example.iesiback.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService{

    private UserRepository repository;
    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;
    
    
    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List) this.repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findById(@NonNull Long id) {
        return repository.findById(id);
    }

    @Transactional
    @Override
    public User save(User user) {
        user.setRoles(getRoles(user));
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        return repository.save(user);
    }
    
    @Override
    @Transactional
    public Optional<User> update(UserRequest user, Long id) {
        Optional<User> userOptional = repository.findById(id);

        if (userOptional.isPresent()) {
            User userDb = userOptional.get();
            userDb.setUserEmail(user.getUserEmail());
            userDb.setUserApellido(user.getUserApellido());
            userDb.setUserNombre(user.getUserNombre());
            userDb.setUserDni(user.getUserDni());
            userDb.setRoles(getRoles(user));
            return Optional.of(repository.save(userDb));
        }
        return Optional.empty();
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private List<Role> getRoles(IUser user) {
        List<Role> roles = new ArrayList<>();
        Optional<Role> optionalRoleUser = roleRepository.findByRoleNombre("ROLE_USER");
        optionalRoleUser.ifPresent(roles::add);

        if (user.isAdmin()) {
            Optional<Role> optionalRoleAdmin = roleRepository.findByRoleNombre("ROLE_ADMIN");
            optionalRoleAdmin.ifPresent(roles::add);
        }
        return roles;
    }

}
