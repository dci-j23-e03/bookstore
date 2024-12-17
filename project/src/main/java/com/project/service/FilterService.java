package com.project.service;

import com.project.entity.BookEntity;
import com.project.entity.CategoryEntity;
import com.project.repository.BookRepository;
import com.project.repository.CategoryRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FilterService {

  @Autowired
  private BookRepository bookRepository;

  @Transactional(readOnly = true)
  public Page<BookEntity> filterBooksByPriceRange(Double minPrice, Double maxPrice,
      org.springframework.data.domain.Pageable pageable) {
    return bookRepository.findByPriceBetween(minPrice, maxPrice, pageable);
  }
  // Get all books sorted by price (low to high)
  @Transactional(readOnly = true)
  public Page<BookEntity> getBooksSortedByPrice(Pageable pageable) {
    return bookRepository.findAllByOrderByPriceAsc(pageable);
  }

  // Get all books without filtering
  @Transactional(readOnly = true)
  public Page<BookEntity> getAllBooks(Pageable pageable) {
    return bookRepository.findAll(pageable); // Default, no filtering
  }

  @Transactional(readOnly = true)
  public List<BookEntity> filterByCategory(String category) {
    return bookRepository.findAllByCategoryContainsIgnoreCase(category, Pageable.unpaged());
  }


  // Search for books by title or author
  @Transactional(readOnly = true)
  public Page<BookEntity> searchBooks(String query, Pageable pageable) {
    return bookRepository
        .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrDescriptionContainsIgnoreCaseOrIsbn(query, query,query,query, query, pageable);
  }
}


