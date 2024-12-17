package com.project.repository;

import com.project.entity.AccountEntity;
import com.project.entity.Basket;
import com.project.entity.OrderDetailsEntity;
import com.project.entity.OrderEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetailsEntity, Long> {

  List<OrderDetailsEntity> findByOrderEntity(OrderEntity orderEntity);

  @Transactional
  List<OrderDetailsEntity> findByAccountEntity(AccountEntity account);


}
