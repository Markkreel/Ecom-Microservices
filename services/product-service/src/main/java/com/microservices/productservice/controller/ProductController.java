package com.microservices.productservice.controller;

import com.microservices.productservice.dto.PagedProductResponse;
import com.microservices.productservice.dto.ProductResponse;
import com.microservices.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<PagedProductResponse> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "0") BigDecimal minPrice,
            @RequestParam(required = false, defaultValue = "999999999") BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort
    ) {
        PagedProductResponse response = productService.getProducts(category, minPrice, maxPrice, page, size, sort);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String productId) {
        ProductResponse response = productService.getProductById(productId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<Map<String, List<String>>> getCategories() {
        List<String> categories = productService.getCategories();
        return ResponseEntity.ok(Map.of("categories", categories));
    }
}