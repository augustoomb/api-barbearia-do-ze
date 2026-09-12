package com.augustoomb.api_barbearia_do_ze.infrastructure.web;

import com.augustoomb.api_barbearia_do_ze.application.professional.CreateProfessionalRequest;
import com.augustoomb.api_barbearia_do_ze.application.professional.UpdateProfessionalRequest;
import com.augustoomb.api_barbearia_do_ze.domain.professional.ProfessionalEntity;
import com.augustoomb.api_barbearia_do_ze.domain.professional.ProfessionalRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProfessionalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProfessionalRepository professionalRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateProfessional() throws Exception {
        CreateProfessionalRequest request = new CreateProfessionalRequest("João Silva", "joao@barbearia.com");

        mockMvc.perform(post("/api/v1/professionals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("João Silva"))
                .andExpect(jsonPath("$.data.email").value("joao@barbearia.com"))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    void shouldRejectProfessionalWithBlankName() throws Exception {
        CreateProfessionalRequest request = new CreateProfessionalRequest("   ", "joao@barbearia.com");

        mockMvc.perform(post("/api/v1/professionals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectProfessionalWithInvalidEmail() throws Exception {
        CreateProfessionalRequest request = new CreateProfessionalRequest("João Silva", "email-invalido");

        mockMvc.perform(post("/api/v1/professionals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectDuplicateEmailCaseInsensitive() throws Exception {
        CreateProfessionalRequest request = new CreateProfessionalRequest("João Silva", "joao@barbearia.com");

        mockMvc.perform(post("/api/v1/professionals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        CreateProfessionalRequest duplicate = new CreateProfessionalRequest("Outro João", "JOAO@barbearia.com");

        mockMvc.perform(post("/api/v1/professionals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldListProfessionals() throws Exception {
        createProfessional("João Silva", "joao@barbearia.com");

        mockMvc.perform(get("/api/v1/professionals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name").value("João Silva"));
    }

    @Test
    void shouldListEmptyProfessionals() throws Exception {
        mockMvc.perform(get("/api/v1/professionals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    void shouldFindProfessionalById() throws Exception {
        String response = createProfessional("João Silva", "joao@barbearia.com");
        String id = objectMapper.readTree(response).get("data").get("id").asText();

        mockMvc.perform(get("/api/v1/professionals/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("João Silva"));
    }

    @Test
    void shouldReturnNotFoundForUnknownId() throws Exception {
        mockMvc.perform(get("/api/v1/professionals/{id}", "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateProfessionalPartially() throws Exception {
        String response = createProfessional("João Silva", "joao@barbearia.com");
        String id = objectMapper.readTree(response).get("data").get("id").asText();

        UpdateProfessionalRequest update = new UpdateProfessionalRequest(null, "joao.novo@barbearia.com");

        mockMvc.perform(patch("/api/v1/professionals/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("joao.novo@barbearia.com"))
                .andExpect(jsonPath("$.data.name").value("João Silva"));
    }

    @Test
    void shouldRejectEmptyUpdateRequest() throws Exception {
        String response = createProfessional("João Silva", "joao@barbearia.com");
        String id = objectMapper.readTree(response).get("data").get("id").asText();

        UpdateProfessionalRequest update = new UpdateProfessionalRequest(null, null);

        mockMvc.perform(patch("/api/v1/professionals/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectUpdateWithNameTooLong() throws Exception {
        String response = createProfessional("João Silva", "joao@barbearia.com");
        String id = objectMapper.readTree(response).get("data").get("id").asText();

        String longName = "a".repeat(121);
        UpdateProfessionalRequest update = new UpdateProfessionalRequest(longName, null);

        mockMvc.perform(patch("/api/v1/professionals/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectUpdateWithEmailTooLong() throws Exception {
        String response = createProfessional("João Silva", "joao@barbearia.com");
        String id = objectMapper.readTree(response).get("data").get("id").asText();

        String longEmail = "a".repeat(250) + "@barbearia.com";
        UpdateProfessionalRequest update = new UpdateProfessionalRequest(null, longEmail);

        mockMvc.perform(patch("/api/v1/professionals/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeactivateAndActivateProfessional() throws Exception {
        String response = createProfessional("João Silva", "joao@barbearia.com");
        String id = objectMapper.readTree(response).get("data").get("id").asText();

        mockMvc.perform(post("/api/v1/professionals/{id}/deactivate", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.active").value(false));

        mockMvc.perform(post("/api/v1/professionals/{id}/activate", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    void shouldBeIdempotentOnActivation() throws Exception {
        String response = createProfessional("João Silva", "joao@barbearia.com");
        String id = objectMapper.readTree(response).get("data").get("id").asText();

        mockMvc.perform(post("/api/v1/professionals/{id}/activate", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    void shouldRejectUpdateWhenEmailConflictsCaseInsensitive() throws Exception {
        createProfessional("João Ativo", "joao@barbearia.com");
        String inactiveResponse = createProfessional("João Inativo", "inativo@barbearia.com");
        String inactiveId = objectMapper.readTree(inactiveResponse).get("data").get("id").asText();

        mockMvc.perform(post("/api/v1/professionals/{id}/deactivate", inactiveId))
                .andExpect(status().isOk());

        UpdateProfessionalRequest update = new UpdateProfessionalRequest(null, "joao@barbearia.com");

        mockMvc.perform(patch("/api/v1/professionals/{id}", inactiveId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldRejectActivationWhenEmailConflictsCaseInsensitive() throws Exception {
        ProfessionalEntity active = ProfessionalEntity.builder()
                .name("João Ativo")
                .email("joao@barbearia.com")
                .active(true)
                .build();

        ProfessionalEntity inactive = ProfessionalEntity.builder()
                .name("João Inativo")
                .email("JOAO@barbearia.com")
                .active(false)
                .build();

        professionalRepository.save(active);
        professionalRepository.save(inactive);

        mockMvc.perform(post("/api/v1/professionals/{id}/activate", inactive.getId()))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldListOneHundredProfessionalsInLessThanTwoSeconds() throws Exception {
        for (int i = 0; i < 100; i++) {
            createProfessional("Profissional " + i, "profissional" + i + "@barbearia.com");
        }

        long start = System.currentTimeMillis();
        mockMvc.perform(get("/api/v1/professionals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(100)));
        long duration = System.currentTimeMillis() - start;

        assertThat(duration).isLessThan(2000);
    }

    private String createProfessional(String name, String email) throws Exception {
        CreateProfessionalRequest request = new CreateProfessionalRequest(name, email);

        return mockMvc.perform(post("/api/v1/professionals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }
}
