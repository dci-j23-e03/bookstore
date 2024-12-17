package com.project.controller;

import com.project.entity.AccountEntity;
import com.project.entity.Basket;
import com.project.entity.OrderEntity;
import com.project.entity.User;
import com.project.repository.OrderDetailsRepository;
import com.project.service.BasketService;
import com.project.service.OrderService;
import com.project.service.UserService;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PaymentController {

    private final UserService userService;
    private final BasketService basketService;
    private final OrderService orderService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private static final BigDecimal DELIVERY_COST = new BigDecimal("5.99");
    private final OrderDetailsRepository orderDetailsRepository;

    /**
     * Constructor to inject dependencies into the PaymentController.
     *
     * @param userService   Service to handle user-related actions
     * @param basketService Service to handle basket-related actions
     * @param orderService  Service to handle order-related actions
     */
    public PaymentController(UserService userService, BasketService basketService,
        OrderService orderService,
        OrderDetailsRepository orderDetailsRepository) {
        this.userService = userService;
        this.basketService = basketService;
        this.orderService = orderService;
        this.orderDetailsRepository = orderDetailsRepository;
    }
    /**
     * Displays the payment page where the user can review their details before checkout.
     * This page is available only to authenticated users.
     *
     * @param model The model that contains the data for the view.
     * @return the payment checkout page view.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/home/basket/checkout")
    public String showPaymentPage(Model model) {
        // Fetch authenticated user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            model.addAttribute("message", "User not found.");
            return "error"; // Redirect to an error page
        }

        // Fetch user's basket
        Basket basket = basketService.getBasketFromLoggedInUser();
        if (basket == null || basket.getBasketDetails().isEmpty()) {
            model.addAttribute("message", "Your basket is empty.");
            return "error"; // Redirect to an error page
        }
        // Calculate the total amount including the delivery cost
        //BigDecimal totalAmount = basket.getTotalAmount().add(DELIVERY_COST);

        // Add necessary attributes to the model
        model.addAttribute("name", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("firstName", user.getAccount().getFirstName());
        model.addAttribute("lastName", user.getAccount().getLastName());
        model.addAttribute("address", user.getAccount().getAddress());
        model.addAttribute("phoneNumber", user.getAccount().getPhoneNumber());
        model.addAttribute("zipCode", user.getAccount().getZipCode());
        model.addAttribute("birthday", user.getAccount().getBirthday());
        model.addAttribute("balance", user.getAccount().getBalance());
        //model.addAttribute("basket", basket);
        //model.addAttribute("basketDetails", basket.getBasketDetails()); // Pass basket details
        //model.addAttribute("totalAmount", totalAmount);

        return "paymentCheckout";
    }


    /**
     * Displays the payment confirmation page with the total amount to be paid.
     * This page allows the user to confirm their payment before proceeding.
     *
     * @param model The model that contains the data for the view.
     * @return the payment confirmation page view.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/home/basket/checkout/confirm")
    public String showPaymentConfirmPage(Model model) {
        // Fetch authenticated user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            model.addAttribute("message", "User not found.");
            return "error"; // Redirect to an error page
        }

        // Fetch user's basket
        Basket basket = basketService.getBasketFromLoggedInUser();
        if (basket == null || basket.getBasketDetails().isEmpty()) {
            model.addAttribute("message", "Your basket is empty.");
            return "error"; // Redirect to an error page
        }
        // Calculate the total amount including the delivery cost
        BigDecimal totalAmount = basket.getTotalAmount().add(DELIVERY_COST);

        // Add necessary attributes to the model
        model.addAttribute("name", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("balance", user.getAccount().getBalance());
        model.addAttribute("basket", basket);
        model.addAttribute("basketDetails", basket.getBasketDetails()); // Pass basket details
        model.addAttribute("totalAmount", totalAmount);

        return "paymentConfirm";
    }


     /**
     * Handles the payment for the order by deducting the total amount from the user's balance.
     * Once the payment is successful, the order is placed, and the user is redirected to the purchase history page.
     *
     * @param model The model that contains the data for the view.
     * @return the purchase history page view after successful payment.
     */
    @PostMapping("/home/basket/pay")
    public String payForOrder(Model model) {
        // Get authenticated user's details
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User with username '" + username + "' not found.");
        }
        AccountEntity account = user.getAccount();
        if (account == null) {
            throw new RuntimeException("Account not found for user");
        }

        // Fetch the basket and validate
        Basket basket = basketService.getBasketFromLoggedInUser();
        if (basket == null || basket.getBasketDetails().isEmpty()) {
            model.addAttribute("error", "Your basket is empty!");
            return "home_test";
        }

        // Calculate total amount from the basket
        BigDecimal totalAmount = basket.getTotalAmount().add(DELIVERY_COST);
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            model.addAttribute("error", "Your basket total is invalid. Please check your basket.");
            return "ShowBasket"; // Return an error page
        }

        // Check if the user's profile is complete
        if (account.getFirstName()==null || account.getLastName()==null || account.getPhoneNumber()==null
            ||account.getAddress()==null) {
            model.addAttribute("error", "Please complete your profile before paying.");
            return "paymentCheckout"; // Return an error page
        }

        if (user.getAccount().getBalance().compareTo(basket.getTotalAmount()) < 0) {
            logger.error("Insufficient balance for user: " + user.getUsername() +
                ", Balance: " + user.getAccount().getBalance() +
                ", Required: " + basket.getTotalAmount());
            model.addAttribute("error", "Insufficient balance. Please add money.");
            return "paymentConfirm";
        }

        // Deduct the total amount from the user's balance
        userService.deductBalance(user, totalAmount);
        OrderEntity order = orderService.createOrder(account, basket);

        // Add success message
        model.addAttribute("message", "Payment successful! Your order has been placed.");
        // Add order data to the model
        model.addAttribute("order", order);
        model.addAttribute("name", account.getUsername());
        return "redirect:/home/purchase-history"; // Return a success page
    }
}






