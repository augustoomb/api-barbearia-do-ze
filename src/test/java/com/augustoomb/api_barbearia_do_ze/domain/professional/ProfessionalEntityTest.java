package com.augustoomb.api_barbearia_do_ze.domain.professional;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProfessionalEntityTest {

    @Test
    void shouldCreateProfessionalWithActiveTrueByDefault() {
        ProfessionalEntity professional = ProfessionalEntity.builder()
                .name("João Silva")
                .email("joao@barbearia.com")
                .build();

        assertThat(professional.isActive()).isTrue();
        assertThat(professional.getName()).isEqualTo("João Silva");
        assertThat(professional.getEmail()).isEqualTo("joao@barbearia.com");
    }
}
