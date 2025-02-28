package com.microservices.productservice.repository;

import com.microservices.productservice.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    @Query("{category: ?0, price: {$gte: ?1, $lte: ?2}}")
    Page<Product> findByCategoryAndPriceRange(
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );

    @Query("{price: {$gte: ?0, $lte: ?1}}")
    Page<Product> findByPriceRange(
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );

    @Query(value = "{}", fields = "{category: 1}")
    List<String> findDistinctCategories();
}