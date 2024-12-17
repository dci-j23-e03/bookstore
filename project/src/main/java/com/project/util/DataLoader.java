//package com.project.util;
//import com.project.entity.User;
//import com.project.repository.UserRepository;
//import java.util.Set;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//@Component
//  public class DataLoader implements CommandLineRunner {
//
//  @Autowired
//  private UserRepository userRepository;
//
//  @Autowired
//  private PasswordEncoder passwordEncoder;
//
//  @Override
//  public void run(String... args) throws Exception {
//    if(userRepository.findByUsername("admin") == null) {
//      User admin = new User();
//      admin.setUsername("admin");
//      admin.setEmail("admin@gmail.com");
//      admin.setPassword(passwordEncoder.encode("admin"));
//      admin.setRoles(Set.of(Role.ADMIN));
//      userRepository.save(admin);
//    }
//  }
//
//  }


