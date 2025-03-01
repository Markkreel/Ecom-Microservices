package com.microservices.productservice.repository;

import com.microservices.productservice.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByCategoryAndPriceRange(
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );

    @Query("SELECT DISTINCT c FROM Category c")
    List<Category> findAllCategories();
}