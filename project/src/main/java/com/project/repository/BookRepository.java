package com.project.repository;

import com.project.entity.BookEntity;
import com.project.entity.CategoryEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

  // Search books by title or author
  Page<BookEntity> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrDescriptionContainsIgnoreCaseOrIsbn(
      String query, String author, String category, String description, String isbn, Pageable pageable);

  // Filter by price range with pagination
  Page<BookEntity> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);

  // Sorting books by title in ascending order
  Page<BookEntity> findAllByOrderByTitleAsc(Pageable pageable);

  // Sorting books by title in descending order
  Page<BookEntity> findAllByOrderByTitleDesc(Pageable pageable);

  // Sorting books by price in ascending order
  Page<BookEntity> findAllByOrderByPriceAsc(Pageable pageable);

  // Sorting books by price in descending order
  Page<BookEntity> findAllByOrderByPriceDesc(Pageable pageable);

//  // A simple findAll method that works with Pageable (handles dynamic sorting)
//  Page<BookEntity> findAll(Pageable pageable);

//// Custom query to filter books based on category
//@Query("SELECT b FROM BookEntity b JOIN b.category c WHERE c = :category")
//   Page<BookEntity> findByCategoryContainingIgnoreCase(@Param("category") String category, Pageable pageable);

  List<BookEntity> findAllByCategoryContainsIgnoreCase(String category, Pageable pageable);
}







