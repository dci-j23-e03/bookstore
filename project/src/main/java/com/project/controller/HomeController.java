package com.project.controller;

import com.project.entity.BookEntity;
import com.project.entity.User;
import com.project.repository.BookRepository;
import com.project.service.HomeService;
import com.project.service.UserService;
import com.project.util.Role;
import java.security.Principal;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

  private final BookRepository bookRepository;

  private final HomeService homeService;
  private final UserService userService;

  public HomeController(BookRepository bookRepository, HomeService homeService,
      UserService userService) {
    this.bookRepository = bookRepository;
    this.homeService = homeService;
    this.userService = userService;
  }

  @GetMapping
  public String redirectToHome () {
    return "redirect:/home";
  }

  @GetMapping("/home")
  public String getHomeScreen(Model model, Principal principal, Pageable pageable) {

    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Set<Role> role = null;
    User user = null;
    if (userService.findByUsername(username) != null) {
      user = userService.findByUsername(username);
      role = user.getRoles();
      System.out.println("role: "+ role);
      model.addAttribute("user", user);
      if (role.contains(Role.ADMIN)) {
        model.addAttribute("role", "admin");
      } else {
        model.addAttribute("role", "user");
        model.addAttribute("balance", user.getAccount().getBalance());
      }
    } else {
      model.addAttribute("user","null");
      model.addAttribute("role", "null");
    }

    Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), 10);
    Page<BookEntity> books = bookRepository.findAll(pageRequest);
    //List<BookEntity> books = bookRepository.findAll();
    model.addAttribute("books", books);

    // Retrieve and set the image data for each book
    for (BookEntity book : books.getContent()) {
      byte[] imageData = book.getImage();
      if (imageData != null) {
        String imageDataBase64 = Base64.getEncoder().encodeToString(imageData);
        book.setImageDataBase64(imageDataBase64);
      }
    }
    model.addAttribute("books", books.getContent());
    model.addAttribute("currentPage", books.getNumber());
    model.addAttribute("totalPages", books.getTotalPages());
    model.addAttribute("totalItems", books.getTotalElements());


    if (role==null || role.contains(Role.USER)) {
      return "home_test";
    } else {
      return  "books";
    }
  }






}
