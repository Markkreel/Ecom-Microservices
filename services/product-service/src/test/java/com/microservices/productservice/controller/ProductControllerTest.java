package com.microservices.productservice.controller;

import com.microservices.productservice.dto.PagedProductResponse;
import com.microservices.productservice.dto.ProductResponse;
import com.microservices.productservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProducts_ShouldReturnPagedResponse() {
        // Arrange
        PagedProductResponse mockResponse = PagedProductResponse.builder()
                .items(Arrays.asList(
                        ProductResponse.builder()
                                .productId("1")
                                .name("Test Product")
                                .price(new BigDecimal("99.99"))
                                .build()
                ))
                .totalItems(1)
                .totalPages(1)
                .build();

        when(productService.getProducts(any(), any(), any(), any(Integer.class), any(Integer.class), any()))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<PagedProductResponse> response = productController.getProducts(
                "category", new BigDecimal("0"), new BigDecimal("100"),
                0, 10, "name");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalItems());
        assertEquals(1, response.getBody().getItems().size());
    }

    @Test
    void getProductById_ShouldReturnProduct() {
        // Arrange
        ProductResponse mockProduct = ProductResponse.builder()
                .productId("1")
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .build();

        when(productService.getProductById("1")).thenReturn(mockProduct);

        // Act
        ResponseEntity<ProductResponse> response = productController.getProductById("1");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("1", response.getBody().getProductId());
    }

    @Test
    void getCategories_ShouldReturnCategoriesList() {
        // Arrange
        List<String> mockCategories = Arrays.asList("Electronics", "Books");
        when(productService.getCategories()).thenReturn(mockCategories);

        // Act
        ResponseEntity<Map<String, List<String>>> response = productController.getCategories();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().get("categories").size());
    }
}