package com.project.controller;
import com.project.entity.BookEntity;
import com.project.entity.User;
import com.project.service.FilterService;
import com.project.service.UserService;
import com.project.util.Role;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class FilterController {

  @Autowired
  private FilterService filterService;

  @Autowired
  private UserService userService;

  @GetMapping("/home/filterByPriceRange")
  public String filterBookByPriceRange(
      @RequestParam(defaultValue = "0") Double minPrice,
      @RequestParam(defaultValue = "1000000") Double maxPrice,
      Pageable pageable,
      Model model) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Set<Role> role = null;
    User user = null;
    if (userService.findByUsername(username) != null) {
      user = userService.findByUsername(username);
      role = user.getRoles();
      System.out.println("role: " + role);
      model.addAttribute("user", user);
      if (role.contains(Role.ADMIN)) {
        model.addAttribute("role", "admin");
      } else {
        model.addAttribute("role", "user");
        model.addAttribute("balance", user.getAccount().getBalance());
      }
    } else {
      model.addAttribute("user", "null");
      model.addAttribute("role", "null");
    }

    Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), 10);

    Page<BookEntity> books = filterService.filterBooksByPriceRange(minPrice, maxPrice, pageRequest);
    model.addAttribute("books", books);
    books.forEach(book -> {
      if (book.getImage() != null && book.getImageDataBase64() == null) {
        String base64Image = encodeImageToBase64(book.getImage());
        book.setImageDataBase64(base64Image); // Populate Base64 data for the image
      } else if (book.getImage() == null) {
        book.setImageDataBase64(null); // Set to null explicitly if no image is present
      }
    });
    model.addAttribute("books", books.getContent());
    model.addAttribute("minPrice", minPrice);
    model.addAttribute("maxPrice", maxPrice);
    model.addAttribute("currentPage", books.getNumber());
    model.addAttribute("totalPages", books.getTotalPages());
    model.addAttribute("totalItems", books.getTotalElements());

    if (role == null || role.contains(Role.USER)) {
      return "home_test";
    } else {
      return "books";
    }

  }

  @GetMapping("/home/filterByPrice")
  public String filterBooksByPrice(
      @RequestParam(defaultValue = "1") Double minPrice,
      @RequestParam(defaultValue = "1000000") Double maxPrice,
      @RequestParam(required = false, defaultValue = "price") String sort,
      // 'sort' can be 'price', 'priceDesc', etc.
      Pageable pageable,
      Model model) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Set<Role> role = null;
    User user = null;
    if (userService.findByUsername(username) != null) {
      user = userService.findByUsername(username);
      role = user.getRoles();
      System.out.println("role: " + role);
      model.addAttribute("user", user);
      if (role.contains(Role.ADMIN)) {
        model.addAttribute("role", "admin");
      } else {
        model.addAttribute("role", "user");
        model.addAttribute("balance", user.getAccount().getBalance());
      }
    } else {
      model.addAttribute("user", "null");
      model.addAttribute("role", "null");
    }

    Page<BookEntity> books;

    // Determine sort order and filter books
    Sort sortOrder;
    switch (sort) {
      case "priceDesc":
        sortOrder = Sort.by(Sort.Order.desc("price"));
        break;
      default: // Default to ascending price
        sortOrder = Sort.by(Sort.Order.asc("price"));
        break;
    }

    // Update pageable with sort order
    pageable = PageRequest.of(pageable.getPageNumber(), 10, sortOrder);

    // Filter books based on the price range and sorting
    if (minPrice != null && maxPrice != null && (minPrice > 0 || maxPrice < 1000000)) {
      books = filterService.filterBooksByPriceRange(minPrice, maxPrice, pageable);
    } else {
      books = filterService.getBooksSortedByPrice(pageable);
    }

    // Populate Base64 image data for books
    books.forEach(book -> {
      if (book.getImage() != null && book.getImageDataBase64() == null) {
        String base64Image = encodeImageToBase64(book.getImage());
        book.setImageDataBase64(base64Image);
      } else if (book.getImage() == null) {
        book.setImageDataBase64(null);
      }
    });

    // Add attributes to the model
    model.addAttribute("books", books.getContent());
    model.addAttribute("currentPage", books.getNumber());
    model.addAttribute("totalPages", books.getTotalPages());
    model.addAttribute("totalItems", books.getTotalElements());
    model.addAttribute("minPrice", minPrice);
    model.addAttribute("maxPrice", maxPrice);
    model.addAttribute("sort", sort);

    if (role == null || role.contains(Role.USER)) {
      return "home_test";
    } else {
      return "books";
    }
  }

  @GetMapping("/home/filterByTitle")
  public String filterBooksByTitle(
      @RequestParam(defaultValue = "1") Double minPrice,
      @RequestParam(defaultValue = "1000000") Double maxPrice,
      @RequestParam(defaultValue = "title") String sort,// 'sort' can be 'title', 'price', etc.
      Pageable pageable,
      Model model) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Set<Role> role = null;
    User user = null;
    if (userService.findByUsername(username) != null) {
      user = userService.findByUsername(username);
      role = user.getRoles();
      System.out.println("role: " + role);
      model.addAttribute("user", user);
      if (role.contains(Role.ADMIN)) {
        model.addAttribute("role", "admin");
      } else {
        model.addAttribute("role", "user");
        model.addAttribute("balance", user.getAccount().getBalance());
      }
    } else {
      model.addAttribute("user", "null");
      model.addAttribute("role", "null");
    }

    // Determine the sorting logic
    Sort sortOrder;
    switch (sort) {
      case "titleDesc":
        sortOrder = Sort.by(Sort.Order.desc("title"));
        break;
      case "titleAsc":
      default:
        sortOrder = Sort.by(Sort.Order.asc("title"));
        break;
    }
    // Apply the sort to the pageable
    pageable = PageRequest.of(pageable.getPageNumber(), 10, sortOrder);

    // Fetch the sorted books
    Page<BookEntity> books = filterService.getAllBooks(pageable); // Update method name if necessary

    books.forEach(book -> {
      if (book.getImage() != null && book.getImageDataBase64() == null) {
        String base64Image = encodeImageToBase64(book.getImage());
        book.setImageDataBase64(base64Image); // Populate Base64 data for the image
      } else if (book.getImage() == null) {
        book.setImageDataBase64(null); // Set to null explicitly if no image is present
      }
    });
    model.addAttribute("books", books.getContent());
    model.addAttribute("currentPage", books.getNumber());
    model.addAttribute("totalPages", books.getTotalPages());
    model.addAttribute("totalItems", books.getTotalElements());
    model.addAttribute("minPrice", minPrice);
    model.addAttribute("maxPrice", maxPrice);
    model.addAttribute("sort", sort);

    if (role == null || role.contains(Role.USER)) {
      return "home_test";
    } else {
      return "books";
    }
  }

  private String encodeImageToBase64(byte[] imageData) {
    return Base64.getEncoder().encodeToString(imageData);
  }
}









