package com.project.service;

import com.project.entity.AccountEntity;
import com.project.entity.User;
import com.project.repository.AccountRepository;
import com.project.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;

  /**
   * Constructor for AccountService class.
   *
   * @param accountRepository Repository to handle CRUD operations for Account entities.
   * @param userRepository Repository to handle CRUD operations for User entities.
   */
  public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
  }

  /**
   * Fetches the Account associated with the provided username.
   *
   * @param username The username of the user whose account is to be retrieved.
   * @return The AccountEntity associated with the username.
   */
  public AccountEntity getAccount (String username) {
    return accountRepository.findByUsername(username);
  }

  /**
   * Updates and saves the provided AccountEntity to the database.
   *
   * @param account The AccountEntity with updated details.
   * @return The updated AccountEntity after being saved.
   */
  public AccountEntity updateAccount(AccountEntity account) {
    return accountRepository.save(account); // Save and return the updated account entity
  }

  /**
   * Retrieves the currently logged-in user's account.
   *
   * @return The AccountEntity associated with the logged-in user.
   */
  @Transactional
  public AccountEntity getLoggedInAccount() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByUsername(username);
    return user.getAccount();
  }
}
