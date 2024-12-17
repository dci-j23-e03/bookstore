package com.project.config;
import com.project.service.UserService;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.NullRequestCache;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  private final UserService userService;

  private final PasswordEncoder passwordEncoder;

  public SecurityConfiguration(UserService userService, PasswordEncoder passwordEncoder) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
  }

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder =
        http.getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder);

    return authenticationManagerBuilder.build();
  }


  @Bean
  public SecurityFilterChain securityFilterChainModal(HttpSecurity http) throws Exception {
    http
        .securityMatcher("/home/**")
        .csrf().disable()
        .authorizeRequests(authorizeRequests ->
            authorizeRequests
                .requestMatchers("/images/**", "/css/**", "/js/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/signup", "/home_test","/home", "/login", "/searchResults", "/filterByPrice","/").permitAll()
                .requestMatchers("/logged-in/", "/redeemGiftCard").authenticated()  // Restrict access to logged-in users
                .requestMatchers("/profile").authenticated()
                .requestMatchers("/logged-in/updateProfile").authenticated()
                .requestMatchers("/home/basket/checkout").authenticated()
                .requestMatchers("/home/payment").authenticated()
                .requestMatchers("/home/basket/pay").authenticated()
                .requestMatchers("/home/purchase-history").authenticated()
                .requestMatchers("/books/**").permitAll()

        )
        .formLogin(formLogin ->
            formLogin
                .loginPage("/home")  // Custom login page
                .defaultSuccessUrl("/", true)  // Redirect after successful login
                .loginProcessingUrl("/home/process-login-modal")  // Form action for login
                .successHandler((request, response, authentication) -> {
                  // Redirect based on roles
                  authentication.getAuthorities().stream()
                      .map(Object::toString)
                      .forEach(authority -> {
                        try {
                          if ("ROLE_ADMIN".equals(authority)) {
                            response.sendRedirect("/admin/dashboard");
                          } else if ("ROLE_USER".equals(authority)) {
                            response.sendRedirect("/home?success");
                          }
                        } catch (IOException e) {
                          e.printStackTrace();
                        }
                      });
                })
                .permitAll())

        .logout(logout ->
            logout
                .logoutUrl("/home/logout")  // URL for logging out
                .logoutSuccessUrl("/home")  // Redirect after successful logout
        )
    ;


    return http.build();

  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .authorizeRequests(authorizeRequests ->
            authorizeRequests
                .requestMatchers("/images/**", "/css/**", "/js/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/signup","/home", "/login", "/searchResults", "/filterByPrice","/").permitAll()
                .requestMatchers("/logged-in/**", "/redeemGiftCard").authenticated()  // Restrict access to logged-in users
                .requestMatchers("/profile").authenticated()
                .requestMatchers("/logged-in/updateProfile").authenticated()
                .requestMatchers("/home/basket/checkout").authenticated()
                .requestMatchers("/home/payment").authenticated()
                .requestMatchers("/home/basket/pay").authenticated()
                .requestMatchers("/books/**").permitAll()
        )

        .formLogin(formLogin ->
            formLogin
                .loginPage("/admin")  // Custom login page
                .defaultSuccessUrl("/logged-in", true)  // Redirect after successful login
                .loginProcessingUrl("/process-login")  // Form action for login
                .successHandler((request, response, authentication) -> {
                  // Redirect based on roles
                  authentication.getAuthorities().stream()
                      .map(Object::toString)
                      .forEach(authority -> {
                        try {
                          if ("ROLE_ADMIN".equals(authority)) {
                            response.sendRedirect("/admin/dashboard");
                          } else if ("ROLE_USER".equals(authority)) {
                            response.sendRedirect("/logged-in");
                          }
                        } catch (IOException e) {
                          e.printStackTrace();
                        }
                      });
                })
                .permitAll()
        )
        .logout(logout ->
            logout
                .logoutUrl("/logout")  // URL for logging out
                .logoutSuccessUrl("/")  // Redirect after successful logout
        );
    return http.build();

  }


}













