package com.project.repository;

import com.project.entity.AccountEntity;
import com.project.entity.Basket;
import com.project.entity.BasketDetails;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {

  Basket findByBasketDetails(BasketDetails basketDetail);
//  Basket findByAccountNumber(String accountNumber);
  Optional<Basket> findByAccountEntity(AccountEntity accountEntity);

 // Optional<Object> findByAccount(AccountEntity attr0);
}