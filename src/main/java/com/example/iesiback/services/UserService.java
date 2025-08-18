package com.example.iesiback.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import com.example.iesiback.entities.User;
import com.example.iesiback.models.UserRequest;
import org.springframework.stereotype.Service;

public interface UserService {

    List<User> findAll();

    Page<User> findAll(Pageable pageable);

    Optional<User> findById(@NonNull Long id);

    User save(User user);

    Optional<User> update(UserRequest user, Long id);

    void deleteById(Long id);
    Optional<User> getAuthenticatedUser();

    User update(String username, User user);
    void deleteByUsername(Long username);
    boolean resetPassword(String username, String newPassword);

    void sendPasswordResetToken(String email);
    boolean resetPasswordWithToken(String token, String newPassword);

    List<User> getUsuariosPorRoles(List<String> roles);
}
