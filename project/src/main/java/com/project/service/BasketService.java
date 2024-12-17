package com.project.service;

import com.project.config.DynamicSchedulingConfig;
import com.project.entity.AccountEntity;
import com.project.entity.Basket;
import com.project.entity.BasketDetails;
import com.project.entity.User;
import com.project.repository.BasketDetailsRepository;
import com.project.repository.BasketRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BasketService {


  @Autowired
  private DynamicSchedulingConfig dynamicSchedulingConfig;
  private final BasketRepository basketRepository;
  private final UserService userService;

  private final BasketDetailsRepository basketDetailsRepository;
  public BasketService(BasketRepository basketRepository, UserService userService,
      BasketDetailsRepository basketDetailsRepository) {
    this.basketRepository = basketRepository;

    this.userService = userService;
    this.basketDetailsRepository = basketDetailsRepository;
  }

  public Basket getBaskedById (Long id) {
    return basketRepository.findById(id).orElse(null);
  }

  public void saveBasketToDatabase (Basket basket) {
    basketRepository.save(basket);
  }


  @Transactional
  public Basket getBasketFromLoggedInUser () {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userService.findByUsername(username);
    AccountEntity accountEntity = user.getAccount();
    return accountEntity.getBasket();
  }


  public BigDecimal calculateTotalAmount (List<BasketDetails> basketDetails) {
    BigDecimal totalAmount = BigDecimal.valueOf(0.0);
    if (basketDetails != null ) {

      for (int i = 0; i < basketDetails.size(); i++) {
        BigDecimal priceOfItemWithQuantity = basketDetails.get(i).getBook().getPrice().multiply(
            BigDecimal.valueOf(basketDetails.get(i).getQuantity()));
        totalAmount = totalAmount.add(priceOfItemWithQuantity);
      }
    }
    return totalAmount;
  }


  public void updateBasketDetails(Long bookId, int quantity) {
    // Fetch the basket detail for the given book ID
    BasketDetails detail = basketDetailsRepository.findByBookId(bookId);

    if (detail != null) {
      // Update quantity and total price
      BigDecimal subtotalBeforeUpdate = detail.getBook().getPrice().multiply(
          BigDecimal.valueOf(detail.getQuantity()));
      detail.setQuantity(quantity);

      //update totalAmount in the basket
      Basket basket = basketRepository.findByBasketDetails(detail);
      BigDecimal subtotalAfterUpdate = detail.getBook().getPrice().multiply(
          BigDecimal.valueOf(quantity));
      BigDecimal totalAmount = (basket.getTotalAmount().subtract(subtotalBeforeUpdate)).add(subtotalAfterUpdate);
      basket.setTotalAmount(totalAmount);
      basketRepository.save(basket);

      // Save the updated detail back to the database
      basketDetailsRepository.save(detail);
    }
  }


  @Transactional
  public void removeBasketDetail(BasketDetails detail) {
    if (detail != null) {
      BasketDetails managedDetail = basketDetailsRepository.findById(detail.getId()).orElse(null);
      if (managedDetail != null) {
        BigDecimal total = managedDetail.getBook().getPrice().multiply(
            BigDecimal.valueOf(managedDetail.getQuantity()));
            Basket basket = basketRepository.findByBasketDetails(managedDetail);
            basket.setTotalAmount(basket.getTotalAmount().subtract(total));
            basketRepository.save(basket);
        basketDetailsRepository.forceDeleteById(managedDetail.getId());
      } else {
        System.out.println("Detail not found in the database");
      }
    } else {
      System.out.println("Detail is null");
    }
  }









}
