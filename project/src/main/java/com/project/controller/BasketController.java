package com.project.controller;

import com.project.entity.Basket;
import com.project.entity.BasketDetails;
import com.project.service.BasketService;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class BasketController {



  private final BasketService basketService;

  public BasketController(BasketService basketService) {
    this.basketService = basketService;
  }


  @GetMapping("/home/basket")
  public String getBasket(Model model) {

    if (!Objects.equals(SecurityContextHolder.getContext().getAuthentication().getName(),
        "anonymousUser")) {
      if (basketService.getBasketFromLoggedInUser().getBasketDetails().size() != 0) {

        System.out.println(basketService.getBasketFromLoggedInUser().getBasketDetails().size());

        Basket basket = basketService.getBasketFromLoggedInUser();
        List<BasketDetails> basketDetails = basket.getBasketDetails();


        for (BasketDetails detail : basketDetails) {
          byte[] imageData = detail.getBook().getImage();
          if (imageData != null) {
            String imageDataBase64 = Base64.getEncoder().encodeToString(imageData);
            detail.getBook().setImageDataBase64(imageDataBase64);
          }
        }

        model.addAttribute("basketDetails", basketDetails);
        model.addAttribute("basket", basket);
        model.addAttribute("balance",basket.getAccountEntity().getBalance());

      } else  {
        model.addAttribute("emptyBasket", "true");
      }

    } else {
      return "redirect:/home?basketLoginError";
    }
    return "ShowBasket";
  }



  @PostMapping("/home/updateBasket/{bookId}")
  public String updateBasket(@PathVariable Long bookId, @RequestParam int quantity, Model model) {
    basketService.updateBasketDetails(bookId, quantity);

    List<BasketDetails> updatedDetails = basketService.getBasketFromLoggedInUser()
        .getBasketDetails();
    model.addAttribute("details", updatedDetails);


    return "redirect:/home/basket";
  }

  @PostMapping("/home/removeFromBasket/{bookId}")
  public String removeFromBasket(@PathVariable Long bookId) {

    List<BasketDetails> details = basketService.getBasketFromLoggedInUser()
        .getBasketDetails();
    BasketDetails detail = null;
    for (BasketDetails basketDetails : details) {
      if (Objects.equals(basketDetails.getBook().getId(), bookId)) {
        detail = basketDetails;
        break;
      }
    }
    if (detail == null) {
      System.out.println("No matching BasketDetails found for bookId: " + bookId);
    } else {
      basketService.removeBasketDetail(detail);
    }
    System.out.println("removed from db");
    return "redirect:/home/basket";
  }


}
