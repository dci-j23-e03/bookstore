package com.project.controller;

import com.project.entity.AccountEntity;
import com.project.entity.BookEntity;
import com.project.entity.OrderDetailsEntity;
import com.project.entity.OrderEntity;
import com.project.entity.User;
import com.project.repository.BookRepository;
import com.project.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.awt.print.Book;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class BookController {


  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private UserService userService;

  @GetMapping("/home")
  public String getBooks(Model model, Pageable pageable) {

    Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), 10);
    Page<BookEntity> books = bookRepository.findAll(pageRequest);
    model.addAttribute("books", books);

    // Retrieve and set the image data for each book
    for (BookEntity book : books) {
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

    return "books";
  }

  @PostMapping("/add")
  public String addBook(
      @RequestParam("title") String title,
      @RequestParam("authorList") List<String> author,
      @RequestParam("categoryList") List<String> category,
      @RequestParam("isbn") String isbn,
      @RequestParam("price") double price,
      @RequestParam("description") String description,
      @RequestParam("image") MultipartFile imageFile,
      RedirectAttributes redirectAttributes) {

    BookEntity book = new BookEntity();
    book.setTitle(title);
    book.setAuthor(author);
    book.setCategory(category);
    book.setIsbn(isbn);
    book.setPrice(BigDecimal.valueOf(price));
    book.setDescription(description);

    try {
      if (!imageFile.isEmpty()) {
        book.setImage(imageFile.getBytes());
      }
    } catch (IOException e) {
      e.printStackTrace();
      redirectAttributes.addFlashAttribute("error", "Image upload failed!");
      return "redirect:/admin/home";
    }
    bookRepository.save(book);
    return "redirect:/admin/home";
  }


  @PostMapping("/edit")
  public String editBook(
      @RequestParam("title") String title,
      @RequestParam("editAuthorList") List<String> author,
      @RequestParam("editCategoryList") List<String> category,
      @RequestParam("isbn") String isbn,
      @RequestParam("price") double price,
      @RequestParam("description") String description,
      @RequestParam("image") MultipartFile imageFile,
      RedirectAttributes redirectAttributes) {

    BookEntity book = new BookEntity();
    book.setTitle(title);
    book.setAuthor(author);
    book.setCategory(category);
    book.setIsbn(isbn);
    book.setPrice(BigDecimal.valueOf(price));
    book.setDescription(description);

    try {
      if (!imageFile.isEmpty()) {
        book.setImage(imageFile.getBytes());
      }
    } catch (IOException e) {
      e.printStackTrace();
      redirectAttributes.addFlashAttribute("error", "Image upload failed!");
      return "redirect:/books";
    }

    bookRepository.save(book);
    return "redirect:/books";
  }


  @PostMapping("/edit/{id}")
  public String editBook(
      @PathVariable("id") Long id,
      @RequestParam("title") String title,
      //@RequestParam("editAuthorList") List<String> author,
      @RequestParam("isbn") String isbn,
      @RequestParam("price") double price,
      @RequestParam("description") String description,
      @RequestParam("image") MultipartFile imageFile,
      HttpServletRequest request,
      HttpServletRequest request2,
      RedirectAttributes redirectAttributes) {

    BookEntity book = bookRepository.findById(id).orElse(null);
    if (book == null) {
      redirectAttributes.addFlashAttribute("error", "Book not found!");
      return "redirect:/admin/home";
    }

    book.setTitle(title);

    // Retrieve all parameters starting with "editCategoryList-"
    Map<String, String[]> parameterMapAuthor = request2.getParameterMap();
    String authorKey = "editAuthorList-" + id;



    if (parameterMapAuthor.containsKey(authorKey)) {
      String authorList = request2.getParameter(authorKey);
      List<String> authors = new ArrayList<>(Arrays.asList(authorList.split(",")));
      System.out.println("Updated authors: " + authors);
      book.setAuthor(authors);
    } else {
      System.out.println("No authors found for book ID: " + id);
    }



    // Retrieve all parameters starting with "editCategoryList-"
    Map<String, String[]> parameterMap = request.getParameterMap();
    String key = "editCategoryList-" + id;



    if (parameterMap.containsKey(key)) {
      String categoryList = request.getParameter(key);
      List<String> categories = new ArrayList<>(Arrays.asList(categoryList.split(",")));
      System.out.println("Updated categories: " + categories);
      book.setCategory(categories);
    } else {
      System.out.println("No categories found for book ID: " + id);
    }

    book.setIsbn(isbn);
    book.setPrice(BigDecimal.valueOf(price));
    book.setDescription(description);

    try {
      if (!imageFile.isEmpty()) {
        book.setImage(imageFile.getBytes());
      }
    } catch (IOException e) {
      e.printStackTrace();
      redirectAttributes.addFlashAttribute("error", "Image upload failed!");
      return "redirect:/admin/home";
    }

    bookRepository.save(book);
    return "redirect:/admin/home";
  }



  @PostMapping("/delete/{id}")
  public String deleteBook (
      @PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
    BookEntity book = bookRepository.findById(id).orElse(null);
    if (book == null) {
      redirectAttributes.addFlashAttribute("error", "Book not found!");
      return "redirect:/admin/home";
    }
    bookRepository.delete(book);
    return "redirect:/admin/home";
  }




  @GetMapping("/image/{id}")
  @ResponseBody
  public byte[] getImage(@PathVariable("id") Long id) {
    BookEntity book = bookRepository.findById(id).orElse(null);
    return (book != null) ? book.getImage() : null;
  }

}
