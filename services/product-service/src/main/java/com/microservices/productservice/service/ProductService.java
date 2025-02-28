package com.microservices.productservice.service;

import com.microservices.productservice.dto.PagedProductResponse;
import com.microservices.productservice.dto.ProductResponse;
import com.microservices.productservice.model.Product;
import com.microservices.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public PagedProductResponse getProducts(String category, BigDecimal minPrice, BigDecimal maxPrice,
                                          int page, int size, String sort) {
        Sort.Direction direction = sort.startsWith("-") ? Sort.Direction.DESC : Sort.Direction.ASC;
        String property = sort.startsWith("-") ? sort.substring(1) : sort;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, property));

        Page<Product> productPage;
        if (category != null) {
            productPage = productRepository.findByCategoryAndPriceRange(category, minPrice, maxPrice, pageable);
        } else {
            productPage = productRepository.findByPriceRange(minPrice, maxPrice, pageable);
        }

        List<ProductResponse> items = productPage.getContent().stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());

        return PagedProductResponse.builder()
                .items(items)
                .totalItems(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .build();
    }

    public ProductResponse getProductById(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToProductResponse(product);
    }

    public List<String> getCategories() {
        return productRepository.findDistinctCategories();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .isAvailable(product.getIsAvailable())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}