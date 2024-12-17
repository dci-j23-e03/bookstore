package com.project.controller;
import com.project.entity.AccountEntity;
import com.project.entity.User;
import com.project.service.UserService;
import com.project.util.SecurityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class AuthenticationController {

  private final UserService userService;

  /**
   * Constructor to inject the UserService dependency.
   *
   * @param userService The service used to manage user-related operations.
   */
  public AuthenticationController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Displays the login page.
   * This method is used to render the login page for unauthenticated users.
   *
   * @return The name of the login view to be rendered.
   */
  @GetMapping("/login")
  public String showLoginPage() {
    return "login";
  }

//  /**
//   * Processes the login attempt.
//   * This method could be further expanded to include authentication logic, but for now,
//   * it just returns the login page after a POST request. You may want to integrate
//   * Spring Security's built-in authentication flow here.
//   *
//   * @param username The entered username.
//   * @param password The entered password.
//   * @return The login page after the post, or possibly redirect to another page after successful login.
//   */
//  @PostMapping("/login")
//  public String showLoginPage(@RequestParam String username, @RequestParam String password) {
//    return "login";
//  }

  /**
   * Displays the logged-in user's profile page.
   * Fetches the logged-in user's information and displays it, including their balance.
   *
   * @param model The model to add attributes to be displayed in the view.
   * @return The name of the logged-in user profile page view to be rendered.
   */
  @GetMapping("/logged-in")
  public String loggedInPage(Model model) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userService.findByUsername(username);
    if (user != null) {
      model.addAttribute("user", user);  // Add the user object to the model
    }
    AccountEntity accountEntity = new AccountEntity();
    if (accountEntity == null) {
      throw new RuntimeException("Account is null");
    }
    model.addAttribute("name", username);
    model.addAttribute("balance", user.getAccount().getBalance());
    return "logged-in"; // The Thymeleaf template to render
  }
}




