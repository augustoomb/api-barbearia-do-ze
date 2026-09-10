package com.augustoomb.api_barbearia_do_ze.infrastructure.web;

import com.augustoomb.api_barbearia_do_ze.application.service.CreateServiceRequest;
import com.augustoomb.api_barbearia_do_ze.application.service.UpdateServiceRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ServiceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateService() throws Exception {
        CreateServiceRequest request = CreateServiceRequest.builder()
                .name("Corte de cabelo")
                .description("Corte social")
                .durationMinutes(30)
                .price(new BigDecimal("35.00"))
                .build();

        mockMvc.perform(post("/api/v1/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Corte de cabelo"))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    void shouldRejectServiceWithBlankName() throws Exception {
        CreateServiceRequest request = CreateServiceRequest.builder()
                .name("   ")
                .durationMinutes(30)
                .price(new BigDecimal("35.00"))
                .build();

        mockMvc.perform(post("/api/v1/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectDuplicateActiveName() throws Exception {
        CreateServiceRequest request = CreateServiceRequest.builder()
                .name("Corte")
                .durationMinutes(30)
                .price(new BigDecimal("35.00"))
                .build();

        mockMvc.perform(post("/api/v1/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldListServices() throws Exception {
        createService("Corte", 30, new BigDecimal("35.00"));

        mockMvc.perform(get("/api/v1/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name").value("Corte"));
    }

    @Test
    void shouldFindServiceById() throws Exception {
        String response = mockMvc.perform(post("/api/v1/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("Corte", 30, new BigDecimal("35.00")))))
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(response).get("data").get("id").asText();

        mockMvc.perform(get("/api/v1/services/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Corte"));
    }

    @Test
    void shouldReturnNotFoundForUnknownId() throws Exception {
        mockMvc.perform(get("/api/v1/services/{id}", "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateServicePartially() throws Exception {
        String response = createService("Corte", 30, new BigDecimal("35.00"));
        String id = objectMapper.readTree(response).get("data").get("id").asText();

        UpdateServiceRequest update = UpdateServiceRequest.builder()
                .price(new BigDecimal("40.00"))
                .build();

        mockMvc.perform(patch("/api/v1/services/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.price").value(40.00))
                .andExpect(jsonPath("$.data.name").value("Corte"));
    }

    @Test
    void shouldDeactivateAndActivateService() throws Exception {
        String response = createService("Corte", 30, new BigDecimal("35.00"));
        String id = objectMapper.readTree(response).get("data").get("id").asText();

        mockMvc.perform(post("/api/v1/services/{id}/deactivate", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.active").value(false));

        mockMvc.perform(post("/api/v1/services/{id}/activate", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    void shouldBeIdempotentOnActivation() throws Exception {
        String response = createService("Corte", 30, new BigDecimal("35.00"));
        String id = objectMapper.readTree(response).get("data").get("id").asText();

        mockMvc.perform(post("/api/v1/services/{id}/activate", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.active").value(true));
    }

    private String createService(String name, int duration, BigDecimal price) throws Exception {
        return mockMvc.perform(post("/api/v1/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest(name, duration, price))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    private CreateServiceRequest buildRequest(String name, int duration, BigDecimal price) {
        return CreateServiceRequest.builder()
                .name(name)
                .durationMinutes(duration)
                .price(price)
                .build();
    }
}
