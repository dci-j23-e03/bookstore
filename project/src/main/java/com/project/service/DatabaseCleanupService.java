package com.project.service;

import com.project.entity.Basket;
import com.project.repository.BasketDetailsRepository;
import com.project.repository.BasketRepository;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleanupService {

  @Autowired
  private BasketDetailsRepository basketDetailsRepository;

  @Autowired
  private BasketRepository basketRepository;

  @PostConstruct
  public void cleanUpDatabase() {
    // Clear BasketDetails
    basketDetailsRepository.forceDeleteAll();

    // Reset totalAmount in all Basket entries
    List<Basket> baskets = basketRepository.findAll();
    for (Basket basket : baskets) {
      basket.setTotalAmount(BigDecimal.valueOf(0.0));
    }
    basketRepository.saveAll(baskets);

    System.out.println("Database clean-up completed: BasketDetails cleared, and Basket totalAmount reset.");
  }
}
