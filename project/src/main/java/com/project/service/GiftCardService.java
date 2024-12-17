package com.project.service;

import com.project.entity.AccountEntity;
import com.project.entity.GiftCardEntity;
import com.project.entity.User;
import com.project.repository.AccountRepository;
import com.project.repository.GiftCardRepository;
import com.project.repository.UserRepository;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GiftCardService {

  @Autowired
  private GiftCardRepository giftCardRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private UserRepository userRepository;

  /**
   * Redeems a gift card by adding its balance to the user's account.
   * The gift card is marked as redeemed after processing.
   *
   * @param cardCode The code of the gift card to redeem.
   * @param userId The ID of the user redeeming the card.
   * @return A success message containing the updated balance.
   * @throws RuntimeException If the gift card is not found, already redeemed, or the user/account is not found.
   */
  public String redeemGiftCard(String cardCode, Long userId) {
    // Fetch the gift card using the card code
    GiftCardEntity giftCard = giftCardRepository.findByCardCode(cardCode)
        .orElseThrow(() -> new RuntimeException("Gift card not found"));
    // Check if the gift card has already been redeemed
    if (giftCard.isRedeemed()) {
      throw new RuntimeException("This gift card has already been redeemed.");
    }
    // Fetch the user's account using the userId
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
    AccountEntity account = user.getAccount();
    if (account == null) {
      throw new RuntimeException("Account not found for user");
    }
    // Ensure the balance is initialized if null
    if (account.getBalance() == null) {
      account.setBalance(BigDecimal.ZERO);
    }
    // Add the gift card balance to the account balance
    account.setBalance(account.getBalance().add(giftCard.getBalance()));
    accountRepository.save(account);
    // Mark the gift card as redeemed
    giftCard.setRedeemed(true);
    giftCard.setAccountEntity(account);
    giftCardRepository.save(giftCard);
    return "Gift card redeemed successfully! Your new balance is: " + account.getBalance();
  }
}

