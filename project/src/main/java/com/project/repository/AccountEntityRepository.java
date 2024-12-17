package com.project.repository;

import com.project.entity.AccountEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountEntityRepository extends JpaRepository<AccountEntity, Long> {
  AccountEntity findByEmail(String email);

 // AccountEntity findByUsername(String username);

}
