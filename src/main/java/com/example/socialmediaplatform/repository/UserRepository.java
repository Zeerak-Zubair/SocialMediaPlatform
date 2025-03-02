package com.example.socialmediaplatform.repository;

import com.example.socialmediaplatform.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findByUsernameContainingOrEmailContainingOrBioContaining(String usernameKeyword, String emailKeyword, String bioKeyword, Pageable pageable);
    Optional<User> findByUsernameOrEmail(String username, String email);
}
