package com.project.service;

import com.project.entity.AccountEntity;
import com.project.entity.Basket;
import com.project.entity.BasketDetails;
import com.project.repository.BasketDetailsRepository;
import com.project.repository.BasketRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BasketDetailsCleanupService {

  @Autowired
  private BasketDetailsRepository basketDetailsRepository;

  @Autowired BasketRepository basketRepository;


  public boolean cleanupBasketDetails(Long basketId, Duration expirationTime) {

    List<BasketDetails> basketDetails = basketDetailsRepository.findByBasketId(basketId);

    System.out.println("BasketDetailsList: " + basketDetails);

    if (basketDetails.isEmpty()) {
      return true; // Stop scheduler if no items exist for this basket
    }

    LocalDateTime expirationThreshold = LocalDateTime.now().minus(expirationTime);
    List<BasketDetails> expiredItems = basketDetails.stream()
        .filter(item -> item.getUpdatedAt().isBefore(expirationThreshold))
        .toList();

    System.out.println("expired items: " + expiredItems);

    if (!expiredItems.isEmpty()) {
      basketDetailsRepository.deleteExpiredItems(expirationThreshold, basketId);
      Basket basket = basketRepository.findById(basketId).orElse(null);
      basket.setTotalAmount(BigDecimal.valueOf(0));
     //basketRepository.save(basket);
    }

    // Check if all items are removed
    return basketDetailsRepository.findByBasketId(basketId).isEmpty();
  }
}



