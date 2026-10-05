package com.augustoomb.api_barbearia_do_ze.infrastructure.web;

import com.augustoomb.api_barbearia_do_ze.application.availability.CreateAvailabilityRequest;
import com.augustoomb.api_barbearia_do_ze.application.availability.UpdateAvailabilityRequest;
import com.augustoomb.api_barbearia_do_ze.application.professional.CreateProfessionalRequest;
import com.augustoomb.api_barbearia_do_ze.domain.availability.AvailabilityRepository;
import com.augustoomb.api_barbearia_do_ze.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
class AvailabilityControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateAvailability() throws Exception {
        String professionalId = createProfessional("João Silva", "joao@barbearia.com");
        CreateAvailabilityRequest request = new CreateAvailabilityRequest(1, "08:00", "12:00");

        mockMvc.perform(post("/api/v1/professionals/{professionalId}/availability", professionalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.professionalId").value(professionalId))
                .andExpect(jsonPath("$.data.dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$.data.startTime").value("08:00:00"))
                .andExpect(jsonPath("$.data.endTime").value("12:00:00"));
    }

    @Test
    void shouldAllowConsecutiveAvailabilityOnSameDay() throws Exception {
        String professionalId = createProfessional("João Silva", "joao@barbearia.com");
        createAvailability(professionalId, 1, "08:00", "12:00");

        CreateAvailabilityRequest request = new CreateAvailabilityRequest(1, "12:00", "17:00");

        mockMvc.perform(post("/api/v1/professionals/{professionalId}/availability", professionalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.startTime").value("12:00:00"))
                .andExpect(jsonPath("$.data.endTime").value("17:00:00"));
    }

    @Test
    void shouldRejectAvailabilityWithInvalidTimeRange() throws Exception {
        String professionalId = createProfessional("João Silva", "joao@barbearia.com");
        CreateAvailabilityRequest request = new CreateAvailabilityRequest(1, "14:00", "12:00");

        mockMvc.perform(post("/api/v1/professionals/{professionalId}/availability", professionalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].code").value("INVALID_AVAILABILITY_PERIOD"))
                .andExpect(jsonPath("$.errors[0].message").value("Horário de término deve ser posterior ao horário de início"));
    }

    @Test
    void shouldRejectOverlappingAvailability() throws Exception {
        String professionalId = createProfessional("João Silva", "joao@barbearia.com");
        createAvailability(professionalId, 1, "08:00", "12:00");

        CreateAvailabilityRequest request = new CreateAvailabilityRequest(1, "09:00", "13:00");

        mockMvc.perform(post("/api/v1/professionals/{professionalId}/availability", professionalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].code").value("OVERLAPPING_AVAILABILITY"))
                .andExpect(jsonPath("$.errors[0].message").value("O período informado conflita com outro período de disponibilidade do profissional"));
    }

    @Test
    void shouldRejectAvailabilityWithInvalidDayOfWeek() throws Exception {
        String professionalId = createProfessional("João Silva", "joao@barbearia.com");
        CreateAvailabilityRequest request = new CreateAvailabilityRequest(0, "08:00", "12:00");

        mockMvc.perform(post("/api/v1/professionals/{professionalId}/availability", professionalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("dayOfWeek"));
    }

    @Test
    void shouldRejectAvailabilityWithDayOfWeekAboveSeven() throws Exception {
        String professionalId = createProfessional("João Silva", "joao@barbearia.com");
        CreateAvailabilityRequest request = new CreateAvailabilityRequest(8, "08:00", "12:00");

        mockMvc.perform(post("/api/v1/professionals/{professionalId}/availability", professionalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("dayOfWeek"));
    }

    @Test
    void shouldRejectAvailabilityForUnknownProfessional() throws Exception {
        CreateAvailabilityRequest request = new CreateAvailabilityRequest(1, "08:00", "12:00");

        mockMvc.perform(post("/api/v1/professionals/{professionalId}/availability", "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].code").value("PROFESSIONAL_NOT_FOUND"))
                .andExpect(jsonPath("$.errors[0].message").value("Profissional não encontrado"));
    }

    @Test
    void shouldListAvailabilitiesOrdered() throws Exception {
        String professionalId = createProfessional("João Silva", "joao@barbearia.com");
        createAvailability(professionalId, 2, "13:00", "17:00");
        createAvailability(professionalId, 1, "08:00", "12:00");

        mockMvc.perform(get("/api/v1/professionals/{professionalId}/availability", professionalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$.data[1].dayOfWeek").value("TUESDAY"));
    }

    @Test
    void shouldListAvailabilitiesFilteredByDayOfWeek() throws Exception {
        String professionalId = createProfessional("João Silva", "joao@barbearia.com");
        createAvailability(professionalId, 2, "13:00", "17:00");
        createAvailability(professionalId, 1, "08:00", "12:00");

        mockMvc.perform(get("/api/v1/professionals/{professionalId}/availability", professionalId)
                        .param("dayOfWeek", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].dayOfWeek").value("MONDAY"));
    }

    @Test
    void shouldListEmptyAvailabilities() throws Exception {
        String professionalId = createProfessional("João Silva", "joao@barbearia.com");

        mockMvc.perform(get("/api/v1/professionals/{professionalId}/availability", professionalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    void shouldUpdateAvailability() throws Exception {
        String professionalId = createProfessional("João Silva", "joao@barbearia.com");
        String availabilityId = createAvailability(professionalId, 1, "08:00", "12:00");

        UpdateAvailabilityRequest request = new UpdateAvailabilityRequest(2, "13:00", "17:00");

        mockMvc.perform(put("/api/v1/professionals/{professionalId}/availability/{availabilityId}", professionalId, availabilityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.dayOfWeek").value("TUESDAY"))
                .andExpect(jsonPath("$.data.startTime").value("13:00:00"))
                .andExpect(jsonPath("$.data.endTime").value("17:00:00"));
    }

    @Test
    void shouldRejectUpdateForUnknownAvailability() throws Exception {
        String professionalId = createProfessional("João Silva", "joao@barbearia.com");
        UpdateAvailabilityRequest request = new UpdateAvailabilityRequest(1, "08:00", "12:00");

        mockMvc.perform(put("/api/v1/professionals/{professionalId}/availability/{availabilityId}", professionalId, "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].code").value("AVAILABILITY_NOT_FOUND"))
                .andExpect(jsonPath("$.errors[0].message").value("Período de disponibilidade não encontrado"));
    }

    @Test
    void shouldRejectUpdateForAvailabilityOfAnotherProfessional() throws Exception {
        String professionalId = createProfessional("João Silva", "joao@barbearia.com");
        String otherProfessionalId = createProfessional("Outro João", "outro@barbearia.com");
        String availabilityId = createAvailability(otherProfessionalId, 1, "08:00", "12:00");

        UpdateAvailabilityRequest request = new UpdateAvailabilityRequest(1, "13:00", "17:00");

        mockMvc.perform(put("/api/v1/professionals/{professionalId}/availability/{availabilityId}", professionalId, availabilityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].code").value("AVAILABILITY_NOT_FOUND"))
                .andExpect(jsonPath("$.errors[0].message").value("Período de disponibilidade não encontrado"));
    }

    @Test
    void shouldDeleteAvailability() throws Exception {
        String professionalId = createProfessional("João Silva", "joao@barbearia.com");
        String availabilityId = createAvailability(professionalId, 1, "08:00", "12:00");

        mockMvc.perform(delete("/api/v1/professionals/{professionalId}/availability/{availabilityId}", professionalId, availabilityId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldRejectDeleteForUnknownAvailability() throws Exception {
        String professionalId = createProfessional("João Silva", "joao@barbearia.com");

        mockMvc.perform(delete("/api/v1/professionals/{professionalId}/availability/{availabilityId}", professionalId, "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].code").value("AVAILABILITY_NOT_FOUND"))
                .andExpect(jsonPath("$.errors[0].message").value("Período de disponibilidade não encontrado"));
    }

    private String createProfessional(String name, String email) throws Exception {
        CreateProfessionalRequest request = new CreateProfessionalRequest(name, email);

        return mockMvc.perform(post("/api/v1/professionals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString()
                .transform(response -> {
                    try {
                        return objectMapper.readTree(response).get("data").get("id").asText();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private String createAvailability(String professionalId, int dayOfWeek, String startTime, String endTime) throws Exception {
        CreateAvailabilityRequest request = new CreateAvailabilityRequest(dayOfWeek, startTime, endTime);

        return mockMvc.perform(post("/api/v1/professionals/{professionalId}/availability", professionalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString()
                .transform(response -> {
                    try {
                        return objectMapper.readTree(response).get("data").get("id").asText();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
