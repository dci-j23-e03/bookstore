package com.project.service;

import com.project.entity.Basket;
import com.project.entity.BasketDetails;
import com.project.repository.BasketDetailsRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BasketDetailsService {

  private final BasketDetailsRepository basketDetailsRepository;

  public BasketDetailsService(BasketDetailsRepository basketDetailsRepository) {
    this.basketDetailsRepository = basketDetailsRepository;
  }

  public BasketDetails getBaskedDetailsById (Long id) {
    return basketDetailsRepository.findById(id).orElse(null);
  }

  public List<BasketDetails> getBasketDetailsFromBasketId (Long basketId) {
    return basketDetailsRepository.findByBasketId(basketId);
  }

@Transactional
  public void saveOrderToDatabase (BasketDetails incompleteOrder) {
    basketDetailsRepository.save(incompleteOrder);
  }

  public void resetExpirationTimeForAllItems(Basket basket) {
    // Set the expiration time to the most recent item's updated time
    LocalDateTime latestUpdatedAt = basket.getBasketDetails().stream()
        .map(BasketDetails::getUpdatedAt)
        .max(LocalDateTime::compareTo)
        .orElse(LocalDateTime.now()); // Default to current time if no items exist

    // Update all BasketDetails with the latest expiration time
    for (BasketDetails detail : basket.getBasketDetails()) {
      detail.setUpdatedAt(latestUpdatedAt);
      saveOrderToDatabase(detail); // Persist the updated BasketDetails
    }
  }


}