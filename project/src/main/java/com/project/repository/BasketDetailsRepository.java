package com.project.repository;

import com.project.entity.BasketDetails;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BasketDetailsRepository extends JpaRepository<BasketDetails, Long> {
// @Modifying
// @Transactional
  @Query("SELECT bd FROM BasketDetails bd JOIN FETCH bd.basket WHERE bd.basket.id = :basketId")
  List<BasketDetails> findByBasketId(@Param("basketId") Long basketId);


  @Modifying
  @Query("DELETE FROM BasketDetails bd WHERE bd.updatedAt <= :expirationThreshold AND bd.basket.id = :basketId")
  void deleteExpiredItems(@Param("expirationThreshold") LocalDateTime expirationThreshold,
      @Param("basketId") Long basketId);


  @Modifying
  @Transactional
  @Query("DELETE FROM BasketDetails bd WHERE bd.id = :id")
  void forceDeleteById(@Param("id") Long id);

  @Modifying
  @Transactional
  @Query("DELETE FROM BasketDetails")
  void forceDeleteAll();




  BasketDetails findByBookIsbn(String isbn);

  @Transactional
  BasketDetails findByBookId(Long id);

}
