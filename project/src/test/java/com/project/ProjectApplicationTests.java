package com.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.project.entities.AccountEntity;
import com.project.entities.OrderEntity;
import com.project.entities.User;
import com.project.repositories.AccountRepository;
import com.project.repositories.OrderRepository;
import com.project.services.UserService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProjectApplicationTests {

  @Test
  void contextLoads() {
  }

  @Autowired
  AccountRepository accountRepository;
  @Autowired
  OrderRepository orderRepository;
  @Autowired
  UserService userService;

  @Test
  public void testOrderEntitySave() {
    AccountEntity account = accountRepository.findById(1L).orElseThrow(); // Replace with valid account
    OrderEntity order = new OrderEntity();
    order.setAccountEntity(account);
    order.setDate(LocalDateTime.now().toString());
    order.setTotalAmount(BigDecimal.valueOf(100.00));

    OrderEntity savedOrder = orderRepository.save(order);
    assertNotNull(savedOrder.getId()); // Ensure ID is generated
  }

  @Test
  public void testDeductBalance_SufficientBalance() {
    User user = new User();
    AccountEntity account = new AccountEntity();
    account.setBalance(new BigDecimal("100.00"));
    user.setAccount(account);

    userService.deductBalance(user, new BigDecimal("25.50"));

    assertEquals(new BigDecimal("74.50"), user.getAccount().getBalance());
  }

  @Test
  public void testDeductBalance_InsufficientBalance() {
    User user = new User();
    AccountEntity account = new AccountEntity();
    account.setBalance(new BigDecimal("50.00"));
    user.setAccount(account);

    userService.deductBalance(user, new BigDecimal("75.00"));
  }



}
