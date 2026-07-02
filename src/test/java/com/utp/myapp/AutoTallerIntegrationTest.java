package com.utp.myapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AutoTallerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private static String authToken;

    @Test
    @Order(1)
    @DisplayName("Register a new tenant + owner user")
    void registerUser() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "email", "test@gmail.com",
                "password", "password",
                "businessName", "AutoTaller Test",
                "phone", "+51999888777",
                "role", "OWNER"
        ));

        String response = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.user.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.data.tenant.businessName").value("AutoTaller Test"))
                .andReturn().getResponse().getContentAsString();

        authToken = objectMapper.readTree(response).get("data").get("token").asText();
    }

    @Test
    @Order(2)
    @DisplayName("Login with valid credentials")
    void loginUser() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "email", "test@gmail.com",
                "password", "password"
        ));

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        authToken = objectMapper.readTree(response).get("data").get("token").asText();
    }

    @Test
    @Order(3)
    @DisplayName("Create a customer (authenticated)")
    void createCustomer() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "name", "Carlos",
                "lastName", "Garcia",
                "dni", "12345678",
                "email", "carlos@email.com",
                "phone", "+51999111222",
                "notes", "Cliente frecuente"
        ));

        mockMvc.perform(post("/api/customers")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Carlos"));
    }

    @Test
    @Order(4)
    @DisplayName("Search customers")
    void searchCustomers() throws Exception {
        mockMvc.perform(get("/api/customers")
                        .header("Authorization", "Bearer " + authToken)
                        .param("query", "Carlos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(5)
    @DisplayName("Register a vehicle")
    void registerVehicle() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "customerId", 1,
                "make", "Toyota",
                "model", "Corolla",
                "year", 2020,
                "plate", "TST-001",
                "color", "Blanco",
                "mileage", 45000,
                "fuelType", "GASOLINE"
        ));

        mockMvc.perform(post("/api/vehicles")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(6)
    @DisplayName("Get analytics dashboard")
    void getDashboard() throws Exception {
        mockMvc.perform(get("/api/analytics/dashboard")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.activeOrders").isNumber());
    }

    @Test
    @Order(7)
    @DisplayName("Get part catalog")
    void getPartCatalog() throws Exception {
        mockMvc.perform(get("/api/catalog/parts")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(8)
    @DisplayName("Access without token returns 403 (Spring Security default)")
    void unauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(9)
    @DisplayName("Login with wrong password returns 400 (BadCredentials handled by GlobalExceptionHandler)")
    void loginWithWrongPassword() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "email", "test@gmail.com",
                "password", "wrongpassword"
        ));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
