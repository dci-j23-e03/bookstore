package com.project.service;

import com.project.entity.BookEntity;
import com.project.repository.BookRepository;
import java.awt.print.Book;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

  private final BookRepository bookRepository;

  public BookService(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  public List<BookEntity> getAllBooks () {
    return bookRepository.findAll();
  }

  public void saveBookToDatabase (BookEntity book) {
    bookRepository.save(book);
  }
  public long getBookCount() {
    return bookRepository.count();
  }

  public BookEntity getBookById (Long bookId) {
    return bookRepository.findById(bookId).orElse(null);
  }





}
