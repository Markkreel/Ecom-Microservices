package com.microservices.productservice.integration;

import com.microservices.productservice.ProductServiceApplication;
import com.microservices.productservice.dto.ProductRequest;
import com.microservices.productservice.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ProductServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class ProductServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    private String baseUrl;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    void cleanup() {
        productRepository.deleteAll();
    }

    @Test
    void shouldCreateProductSuccessfully() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(99.99))
                .category("Electronics")
                .stockQuantity(10)
                .build();

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/api/products",
                request,
                String.class
        );

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(productRepository.findByName("Test Product").isPresent());
    }

    @Test
    void shouldUpdateProductStockSuccessfully() {
        // Given
        ProductRequest createRequest = ProductRequest.builder()
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(99.99))
                .category("Electronics")
                .stockQuantity(10)
                .build();

        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/products",
                createRequest,
                String.class
        );
        String productId = createResponse.getBody();

        // When
        int newStockQuantity = 5;
        ResponseEntity<Void> updateResponse = restTemplate.patchForObject(
                baseUrl + "/api/products/" + productId + "/stock",
                newStockQuantity,
                Void.class
        );

        // Then
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        var updatedProduct = productRepository.findById(productId).orElseThrow();
        assertEquals(newStockQuantity, updatedProduct.getStockQuantity());
    }
}