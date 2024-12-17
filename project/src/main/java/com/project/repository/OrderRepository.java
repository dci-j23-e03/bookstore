package com.project.repository;

import com.project.entity.AccountEntity;
import com.project.entity.OrderEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

  List<OrderEntity> findByAccountEntity(AccountEntity accountEntity);
  // Fetch the top 10 orders ordered by order date in descending order
  List<OrderEntity> findTop10ByOrderByIdDesc();
  @Transactional
  @Query("SELECT o FROM OrderEntity o WHERE o.accountEntity = :account ORDER BY o.Date DESC")
  List<OrderEntity> findByAccountEntityOrderByDateDesc(@Param("account") AccountEntity account);

}
