package com.project.service;

import com.project.entity.BookEntity;
import com.project.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class HomeService {

  private final BookRepository bookRepository;


  public HomeService(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }


  public Page<BookEntity> bookList(Pageable pageable) {
    return bookRepository.findAll(pageable);
  }



}
