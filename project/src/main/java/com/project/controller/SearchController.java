package com.project.controller;
import com.project.entity.BookEntity;
import com.project.entity.User;
import com.project.service.FilterService;
import com.project.service.UserService;
import com.project.util.Role;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {


  private final FilterService filterService;

  private final UserService userService;

  /**
   * Constructor to initialize the dependencies for the SearchController.
   *
   * @param filterService Service to handle filtering of books.
   * @param userService Service to manage user-related operations.
   */
  public SearchController(FilterService filterService, UserService userService) {
    this.filterService = filterService;
    this.userService = userService;
  }

  /**
   * Handles the request to view search results for the home page.
   *
   * This method processes the search query, pagination, filtering by price range, and sorting.
   * The results are then displayed on the homepage.
   *
   * @param model The Model object to pass attributes to the view.
   * @param query The search query entered by the user.
   * @param page The current page number for pagination (default is 0).
   * @param size The page size (default is 10).
   * @param minPrice The minimum price filter (optional).
   * @param maxPrice The maximum price filter (optional).
   * @param sort The sorting parameter (optional).
   * @param pageable The Pageable object containing page and size information.
   * @return The name of the Thymeleaf template to render the search results page.
   */
  @GetMapping("/home/searchResults")
  public String getHomeScreen(Model model,
      @RequestParam("query") String query,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size,
      @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
      @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
      @RequestParam(value = "sort", required = false) String sort,
      Pageable pageable) {

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
    Page<BookEntity> books = filterService.searchBooks(query, PageRequest.of(page, size));
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
    model.addAttribute("totalItems", (int) books.getTotalElements());
    model.addAttribute("query", query);
    model.addAttribute("minPrice", minPrice);
    model.addAttribute("maxPrice", maxPrice);
    model.addAttribute("sort", sort);

      return "home_test";

  }

  /**
   * Handles the request to view search results for the admin page.
   *
   * This method processes the search query, pagination, filtering by price range, and sorting for admins.
   * The results are displayed on the admin page.
   *
   * @param model The Model object to pass attributes to the view.
   * @param query The search query entered by the admin.
   * @param page The current page number for pagination (default is 0).
   * @param size The page size (default is 10).
   * @param minPrice The minimum price filter (optional).
   * @param maxPrice The maximum price filter (optional).
   * @param sort The sorting parameter (optional).
   * @param pageable The Pageable object containing page and size information.
   * @return The name of the Thymeleaf template to render the search results page for admins.
   */
  @GetMapping("/admin/searchResults")
  public String getAdminScreen(Model model,
      @RequestParam("query") String query,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size,
      @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
      @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
      @RequestParam(value = "sort", required = false) String sort,
      Pageable pageable) {

    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Set<Role> role = null;
    User user = null;
    if (userService.findByUsername(username) != null) {
      user = userService.findByUsername(username);
      role = user.getRoles();
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
    Page<BookEntity> books = filterService.searchBooks(query, PageRequest.of(page, size));
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
    model.addAttribute("totalItems", (int) books.getTotalElements());
    model.addAttribute("minPrice", minPrice);
    model.addAttribute("maxPrice", maxPrice);
    model.addAttribute("sort", sort);

    return "books";
  }




}
