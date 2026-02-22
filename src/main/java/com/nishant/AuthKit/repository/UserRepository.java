package com.nishant.AuthKit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nishant.AuthKit.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    default Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return findByUsername(usernameOrEmail)
                .or(() -> findByEmail(usernameOrEmail));
    }
}
