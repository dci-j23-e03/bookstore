package com.project.controller;

import com.project.config.DynamicSchedulingConfig;
import com.project.entity.AccountEntity;
import com.project.entity.Basket;
import com.project.entity.BasketDetails;
import com.project.entity.BookEntity;
import com.project.service.AccountService;
import com.project.service.BasketDetailsService;
import com.project.service.BasketService;
import com.project.service.BookService;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/home")
public class BasketUserController {


  private final BasketDetailsService basketDetailsService;

  private final BasketService basketService;

  private  final AccountService accountService;


  private final BookService bookService;

  private final DynamicSchedulingConfig dynamicSchedulingConfig;

  public BasketUserController(BasketDetailsService basketDetailsService,
      BasketService basketService, AccountService accountService, BookService bookService,
      DynamicSchedulingConfig dynamicSchedulingConfig) {
    this.basketDetailsService = basketDetailsService;
    this.basketService = basketService;
    this.accountService = accountService;
    this.bookService = bookService;
    this.dynamicSchedulingConfig = dynamicSchedulingConfig;
  }


  @PostMapping("/addToBasket/{bookId}")
  public String addToBasket(
      @PathVariable("bookId") Long bookId,
      @RequestParam Integer quantity,
      Model model
  ) {

    AccountEntity accountEntity = accountService.getLoggedInAccount();


    // Find the book by its ID
    BookEntity book = bookService.getBookById(bookId);
    if (book == null) {
      model.addAttribute("error", "Book not found");
      return "redirect:/home?bookNotFoundError";
    }

    // Retrieve the basket for the currently logged-in user
    Basket basket = basketService.getBasketFromLoggedInUser();
    if (basket == null) {
      // If no basket exists, create a new one
      basket = new Basket();
      basket.setBasketDetails(new ArrayList<>());
     // basket.setAccountEntity(accountService.getLoggedInAccount());
      basket.setAccountEntity(accountEntity);

      // Associate the new basket with the user's account
//      String username = SecurityContextHolder.getContext().getAuthentication().getName();
//      User user = userService.findByUsername(username);
//      AccountEntity accountEntity = user.getAccount();
      accountEntity.setBasket(basket);

      basketService.saveBasketToDatabase(basket);
      accountService.updateAccount(accountEntity);
    }

    // Check if the book is already in the basket
    Optional<BasketDetails> existingDetail = basket.getBasketDetails().stream()
        .filter(detail -> detail.getBook().getIsbn().equals(book.getIsbn()))
        .findFirst();

    if (existingDetail.isPresent()) {
      BasketDetails detail = existingDetail.get();
      detail.setQuantity(detail.getQuantity() + quantity);
      detail.setUpdatedAt(LocalDateTime.now());
      basketDetailsService.saveOrderToDatabase(detail);
    } else {
      BasketDetails newDetail = new BasketDetails();
      newDetail.setBasket(basket);
      newDetail.setBook(book);
      newDetail.setQuantity(quantity);
      newDetail.setUpdatedAt(LocalDateTime.now());
      basketDetailsService.saveOrderToDatabase(newDetail);
      basket.getBasketDetails().add(newDetail);
      basketService.saveBasketToDatabase(basket);
    }

    Long basketId = basket.getId();
    List<BasketDetails> basketDetailsList = basketDetailsService.getBasketDetailsFromBasketId(basketId);
    BigDecimal totalAmount = basketService.calculateTotalAmount(basketDetailsList);
    basket.setTotalAmount(totalAmount);
    basketService.saveBasketToDatabase(basket);


    basketDetailsService.resetExpirationTimeForAllItems(basket);
    // Start the cleanup scheduler for this basket
    dynamicSchedulingConfig.startBasketDetailsCleanupScheduler(basket.getId(), Duration.ofMinutes(5));

    return "redirect:/home?successfullyAddedToBasket";
  }




}
