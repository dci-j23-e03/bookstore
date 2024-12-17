package com.project.repository;

import com.project.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  User findByUsername(String username);

  boolean existsByUsername(String username);
  boolean existsByEmail(String email);
  Optional<User> findById(Long id);

}

