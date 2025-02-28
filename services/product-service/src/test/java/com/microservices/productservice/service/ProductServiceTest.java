package com.microservices.productservice.service;

import com.microservices.productservice.dto.PagedProductResponse;
import com.microservices.productservice.dto.ProductResponse;
import com.microservices.productservice.model.Product;
import com.microservices.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProducts_WithCategory_ShouldReturnFilteredProducts() {
        // Arrange
        Product mockProduct = Product.builder()
                .id("1")
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .category("Electronics")
                .build();

        Page<Product> mockPage = new PageImpl<>(Arrays.asList(mockProduct));
        when(productRepository.findByCategoryAndPriceRange(
                any(), any(), any(), any(Pageable.class)))
                .thenReturn(mockPage);

        // Act
        PagedProductResponse response = productService.getProducts(
                "Electronics", new BigDecimal("0"), new BigDecimal("100"),
                0, 10, "name");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalItems());
        assertEquals(1, response.getItems().size());
        assertEquals("1", response.getItems().get(0).getProductId());
    }

    @Test
    void getProducts_WithoutCategory_ShouldReturnAllProducts() {
        // Arrange
        Product mockProduct = Product.builder()
                .id("1")
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .build();

        Page<Product> mockPage = new PageImpl<>(Arrays.asList(mockProduct));
        when(productRepository.findByPriceRange(
                any(), any(), any(Pageable.class)))
                .thenReturn(mockPage);

        // Act
        PagedProductResponse response = productService.getProducts(
                null, new BigDecimal("0"), new BigDecimal("100"),
                0, 10, "name");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalItems());
        assertEquals(1, response.getItems().size());
    }

    @Test
    void getProductById_ExistingProduct_ShouldReturnProduct() {
        // Arrange
        Product mockProduct = Product.builder()
                .id("1")
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .build();

        when(productRepository.findById("1")).thenReturn(Optional.of(mockProduct));

        // Act
        ProductResponse response = productService.getProductById("1");

        // Assert
        assertNotNull(response);
        assertEquals("1", response.getProductId());
        assertEquals("Test Product", response.getName());
    }

    @Test
    void getProductById_NonExistingProduct_ShouldThrowException() {
        // Arrange
        when(productRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            productService.getProductById("nonexistent");
        });
    }

    @Test
    void getCategories_ShouldReturnAllCategories() {
        // Arrange
        List<String> mockCategories = Arrays.asList("Electronics", "Books");
        when(productRepository.findDistinctCategories()).thenReturn(mockCategories);

        // Act
        List<String> categories = productService.getCategories();

        // Assert
        assertNotNull(categories);
        assertEquals(2, categories.size());
        assertTrue(categories.contains("Electronics"));
        assertTrue(categories.contains("Books"));
    }
}