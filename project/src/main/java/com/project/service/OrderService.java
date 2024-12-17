package com.project.service;


import com.project.entity.AccountEntity;
import com.project.entity.Basket;
import com.project.entity.BasketDetails;
import com.project.entity.OrderDetailsEntity;
import com.project.entity.OrderEntity;
import com.project.entity.User;
import com.project.repository.OrderDetailsRepository;
import com.project.repository.OrderRepository;
import com.project.repository.PaymentRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

  @Autowired
  private OrderRepository orderRepository;
  @Autowired
  private OrderDetailsRepository orderDetailsRepository;
  @Autowired
  private PaymentRepository paymentRepository;

  public List<OrderEntity> getOrdersByUser(User user) {
    return orderRepository.findByAccountEntity(user.getAccount()); // Assumes you have an order repository
  }

  @Transactional
  public long getOrderCount() {
    return orderRepository.count();
  }

  @Transactional
  public List<OrderEntity> getRecentOrders() {
    return orderRepository.findTop10ByOrderByIdDesc();  // Assumes OrderEntity has a field 'orderDate'
  }

//  @Transactional
//  public OrderEntity createOrder(AccountEntity account, Basket basket) {
//    if (basket == null || basket.getBasketDetails().isEmpty()) {
//      throw new RuntimeException("Basket is empty, cannot create order");
//    }
//    if (account == null) {
//      throw new RuntimeException("Account is null, cannot create order");
//    }
//
//    // Create and save the order entity
//    OrderEntity order = new OrderEntity();
//    order.setAccountEntity(account);
//    order.setDate(LocalDateTime.now().toString());
//    order.setTotalAmount(basket.getTotalAmount());
//    order = orderRepository.save(order);
//
//    // Create and save order details
//    // Set order reference
//    // Set account reference
//    List<OrderDetailsEntity> orderDetailsList = new ArrayList<>();
//    for (BasketDetails basketDetails : basket.getBasketDetails()) {
//      OrderDetailsEntity orderDetails = new OrderDetailsEntity();
//      orderDetails.setBook(basketDetails.getBook());
//      orderDetails.setQuantity(basketDetails.getQuantity());
//      orderDetails.setOrderEntity(order);  // Set order reference
//      orderDetails.setAccountEntity(account); // Set account reference
//      OrderDetailsEntity apply = orderDetails;
//      orderDetailsList.add(apply);
//    }
//
//    orderDetailsRepository.saveAll(orderDetailsList);
//
//    return order;
//  }

//  @Transactional
//  public List<OrderEntity> findAllOrdersByAccountSortedByDateDesc(AccountEntity account) {
//    List<OrderEntity> orders = orderRepository.findByAccountEntity(account);
//    return orders.stream()
//        .sorted((o1, o2) -> LocalDateTime.parse(o2.getDate()).compareTo(LocalDateTime.parse(o1.getDate())))
//        .collect(Collectors.toList());
//  }

  public OrderEntity createOrder(AccountEntity account, Basket basket) {
    if (basket == null || basket.getBasketDetails().isEmpty()) {
      throw new RuntimeException("Basket is empty, cannot create order");
    }
    if (account == null) {
      throw new RuntimeException("Account is null, cannot create order");
    }

    // Create and save the order entity
    OrderEntity order = new OrderEntity();
    order.setAccountEntity(account);


    LocalDateTime localDateTime = LocalDateTime.now();
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy HH:mm:ss a");
    String orderDate = localDateTime.format(dateTimeFormatter);
    order.setDate(orderDate);

   // order.setDate(LocalDateTime.now().toString()); // Store current timestamp
    order.setTotalAmount(basket.getTotalAmount()); // Set total amount from basket
    order = orderRepository.save(order);  // Save the order

    // Create and save order details
    // Prepare a list to hold order details
    List<OrderDetailsEntity> orderDetailsList = new ArrayList<>();

    // Loop through the basket details to create the order details for each item
    for (BasketDetails basketDetails : basket.getBasketDetails()) {
      // Create each OrderDetailsEntity based on basket details
      OrderDetailsEntity orderDetails = new OrderDetailsEntity();
      orderDetails.setBook(basketDetails.getBook());
      orderDetails.setQuantity(basketDetails.getQuantity());  // Set the quantity from the basket
      orderDetails.setOrderEntity(order);  // Set the order reference
      orderDetails.setAccountEntity(account);  // Set the account reference

      // Ensure that quantity is a positive number
      if (orderDetails.getQuantity() <= 0) {
        throw new IllegalArgumentException("Quantity must be greater than zero.");
      }

      // Add order details to the list
      orderDetailsList.add(orderDetails);
    }

    // Save all order details at once
    orderDetailsRepository.saveAll(orderDetailsList);

    // Return the created order
    return order;
  }


}







