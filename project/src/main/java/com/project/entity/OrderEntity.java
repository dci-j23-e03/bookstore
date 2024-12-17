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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_id")
  private Long id;

  private String Date;

  private BigDecimal totalAmount;


  @ManyToOne
  @JoinColumn(name = "account_id", nullable = false)
  private AccountEntity accountEntity;


@OneToMany(mappedBy = "orderEntity", cascade = CascadeType.ALL, orphanRemoval = true)
private List<OrderDetailsEntity> orderDetails;

  @OneToOne
  @JoinColumn(name = "order_id", nullable = false)
  private PaymentEntity payment;




}
