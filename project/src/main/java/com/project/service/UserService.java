package com.project.service;

import com.project.entity.AccountEntity;
import com.project.entity.User;
import com.project.repository.AccountRepository;
import com.project.repository.UserRepository;
import com.project.util.Role;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AccountRepository accountRepository;
  private final Logger logger = LoggerFactory.getLogger(UserService.class);


  @Autowired
  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AccountService accountService,
      AccountRepository accountRepository) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.accountRepository = accountRepository;
  }

  // Method to load user by username for authentication
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException("User not found");
    }

    Set<GrantedAuthority> authorities = user.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())) // Prefix "ROLE_"
        .collect(Collectors.toSet());

    logger.info("Loading user: {}", user.getUsername());
    user.getRoles().forEach(role -> logger.info("Role: {}", role));

    return new org.springframework.security.core.userdetails.User(
        user.getUsername(),
        user.getPassword(),
        authorities
    );
  }

  // Find a user by username (not necessarily for authentication)
  @Transactional
  public User findByUsername(String username) {
    return userRepository.findByUsername(username);
  }


  public void saveUser(String username, String email, String password, Role role) {
    // Validate inputs
    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("Username already exists.");
    }
    if (userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("Email already exists.");
    }
    if (role == null) {
      throw new IllegalArgumentException("Role must not be null.");
    }
    // Create the user and associated account
    User newUser = new User();
    newUser.setUsername(username);
    newUser.setEmail(email);
    newUser.setPassword(passwordEncoder.encode(password));
    newUser.setRoles(Set.of(role));

    AccountEntity account = createAccountForUser(); // Decoupled account creation
    newUser.setAccount(account);

    // Save the user
    userRepository.save(newUser);

    // Log the action
    logger.info("New user registered: {}", username);
  }

  private AccountEntity createAccountForUser() {
    return new AccountEntity();
  }

  public long getUserCount() {
    return userRepository.count();
  }

@Transactional
  public Long getAuthenticatedUserId() {
    // Retrieve the authenticated username from the security context
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    // Fetch the custom User entity from the database using the username
    User user = userRepository.findByUsername(username);

    if (user == null) {
      throw new RuntimeException("User not found for username: " + username);
    }
    return user.getId();
  }


  public void deductBalance(User user, BigDecimal amount) {
      // Ensure account and balance are not null
      if (user.getAccount() == null || user.getAccount().getBalance() == null) {
        throw new IllegalArgumentException("Account or balance cannot be null");
      }

      // Deduct the amount using BigDecimal.subtract()
      BigDecimal updatedBalance = user.getAccount().getBalance().subtract(amount);

      // Ensure the updated balance is not negative
      if (updatedBalance.compareTo(BigDecimal.ZERO) < 0) {
        throw new IllegalArgumentException("Insufficient balance for deduction");
      }
      // Update the balance and save the user
      user.getAccount().setBalance(updatedBalance);
      userRepository.save(user);
  }

  @Transactional
  public void deleteAccountAndProfile(String username) {
    // Find the user by username
    User user = userRepository.findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException("User not found for username: " + username);
    }

    // Log the deletion action
    logger.info("Deleting account and profile for user: {}", username);

    // Delete associated account if exists
    AccountEntity account = user.getAccount();
    if (account != null) {
      accountRepository.delete(account);
      logger.info("Deleted associated account for user: {}", username);
    }

    // Delete the user
    userRepository.delete(user);
    logger.info("Deleted user profile for user: {}", username);
  }



}








