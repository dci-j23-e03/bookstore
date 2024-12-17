//package com.project.util;
//
//import com.project.entity.GiftCardEntity;
//import com.project.repository.GiftCardRepository;
//import java.math.BigDecimal;
//import java.util.List;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class GiftCardDataLoader implements CommandLineRunner {
//
//  private final GiftCardRepository giftCardRepository;
//
//  public GiftCardDataLoader(GiftCardRepository giftCardRepository) {
//    this.giftCardRepository = giftCardRepository;
//  }
//
//
//  @Override
//  public void run(String... args) throws Exception {
//    // Create gift cards
//    GiftCardEntity giftCard1 = GiftCardEntity.builder()
//        .cardCode("GC14456")
//        .balance(new BigDecimal("50.00"))
//        .redeemed(false)
//        .build();
//
//    GiftCardEntity giftCard2 = GiftCardEntity.builder()
//        .cardCode("GC89585")
//        .balance(new BigDecimal("100.00"))
//        .redeemed(false)
//        .build();
//
//    GiftCardEntity giftCard3 = GiftCardEntity.builder()
//        .cardCode("GC75757")
//        .balance(new BigDecimal("20.00"))
//        .redeemed(false)
//        .build();
//
//    GiftCardEntity giftCard4 = GiftCardEntity.builder()
//        .cardCode("GC365587")
//        .balance(new BigDecimal("75.00"))
//        .redeemed(false)
//        .build();
//
//    GiftCardEntity giftCard5 = GiftCardEntity.builder()
//        .cardCode("GC785868")
//        .balance(new BigDecimal("150.00"))
//        .redeemed(false)
//        .build();
//
//    GiftCardEntity giftCard6 = GiftCardEntity.builder()
//        .cardCode("GC25857")
//        .balance(new BigDecimal("200.00"))
//        .redeemed(false)
//        .build();
//
//    // Save the gift cards to the database
//    giftCardRepository.saveAll(
//        List.of(giftCard1, giftCard2, giftCard3, giftCard4, giftCard5, giftCard6));
//  }
//}


