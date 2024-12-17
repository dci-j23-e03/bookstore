package com.project.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Basket {

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }


  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }


  public AccountEntity getAccountEntity() {
    return accountEntity;
  }

  public void setAccountEntity(AccountEntity accountEntity) {
    this.accountEntity = accountEntity;
  }

  public List<BasketDetails> getBasketDetails() {
    return basketDetails;
  }

  public void setBasketDetails(List<BasketDetails> basketDetails) {
    this.basketDetails = basketDetails;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "basket_id")
  private Long id;

  @OneToOne
  @JoinColumn(name = "account_id")
  private AccountEntity accountEntity;

 @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
 // @OneToMany
  @JoinColumn
  private List<BasketDetails> basketDetails;

  private BigDecimal totalAmount;


//  @OneToOne
//  @JoinColumn(name = "order_id")
//  private PaymentEntity payment;


}
