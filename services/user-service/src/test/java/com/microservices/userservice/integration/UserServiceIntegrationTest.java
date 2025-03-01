package com.microservices.userservice.integration;

import com.microservices.userservice.UserServiceApplication;
import com.microservices.userservice.dto.UserRegistrationRequest;
import com.microservices.userservice.dto.UserLoginRequest;
import com.microservices.userservice.repository.UserRepository;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class UserServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

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
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        // Given
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email("test@example.com")
                .password("password123")
                .name("Test User")
                .build();

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/api/users/register",
                request,
                String.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(userRepository.existsByEmail("test@example.com"));
    }

    @Test
    void shouldLoginUserSuccessfully() {
        // Given
        UserRegistrationRequest registrationRequest = UserRegistrationRequest.builder()
                .email("test@example.com")
                .password("password123")
                .name("Test User")
                .build();

        restTemplate.postForEntity(
                baseUrl + "/api/users/register",
                registrationRequest,
                String.class
        );

        UserLoginRequest loginRequest = UserLoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/api/users/login",
                loginRequest,
                String.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}