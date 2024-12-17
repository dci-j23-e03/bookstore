package com.project.controller;

import com.project.entity.User;
import com.project.repository.UserRepository;
import com.project.service.AccountService;
import com.project.service.GiftCardService;
import com.project.service.UserService;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GiftCardController {

  @Autowired
  private UserService userService;

  @Autowired
  private AccountService accountService;

  @Autowired
  private GiftCardService giftCardService;
  @Autowired
  private UserRepository userRepository;
  private static final Logger logger = LoggerFactory.getLogger(GiftCardController.class);

  /**
   * Displays the page for adding money to the user's account via a gift card.
   * This is an authenticated page where users can enter their gift card code.
   *
   * @param model The model object to be passed to the view.
   * @return The name of the view to be rendered, "redeem" for the gift card redemption page.
   */
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/redeemGiftCard")
  public String showAddMoneyPage(Model model) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userService.findByUsername(username);

    return "redeem"; // This is the Thymeleaf template for adding money
  }


  /**
   * Redeems a gift card when the user submits the form with the gift card code.
   * This method is only accessible by authenticated users.
   *
   * @param cardCode The code of the gift card entered by the user.
   * @param model The model object to pass the results or error message to the view.
   * @return The name of the view to render, either "logged-in" on success or "redeem" on failure.
   */
  @PreAuthorize("isAuthenticated()") // Only allow authenticated users to access this method
  @PostMapping("/redeemGiftCard")
  public String redeemGiftCard(@RequestParam String cardCode, Model model) {
    try {
      Long userId = userService.getAuthenticatedUserId();
      // Redeem the gift card and fetch the updated balance
      String message = giftCardService.redeemGiftCard(cardCode, userId);

      // Fetch the user from the database
      User user = userRepository.findById(userId)
          .orElseThrow(() -> new RuntimeException("User not found"));

      // Log the balance to verify it's being correctly passed
      logger.info("Updated balance for user: " + user.getUsername() + " is " + user.getAccount().getBalance());
      // Add attributes to the model
      model.addAttribute("message", message);
      model.addAttribute("username", user.getUsername());
      model.addAttribute("user", user);
      model.addAttribute("balance", user.getAccount().getBalance());
      return "logged-in"; // Redirect to the logged-in page
    } catch (Exception e) {
      model.addAttribute("error", e.getMessage());
      return "redeem"; // Show the error page
    }
  }
}



