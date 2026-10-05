package com.augustoomb.api_barbearia_do_ze.domain.availability;

import com.augustoomb.api_barbearia_do_ze.domain.professional.ProfessionalEntity;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class AvailabilityEntityTest {

    @Test
    void shouldCreateAvailabilityEntity() {
        ProfessionalEntity professional = ProfessionalEntity.builder()
                .id(java.util.UUID.randomUUID())
                .name("João")
                .email("joao@barbearia.com")
                .build();

        AvailabilityEntity entity = AvailabilityEntity.builder()
                .professional(professional)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(12, 0))
                .build();

        assertThat(entity.getProfessional()).isEqualTo(professional);
        assertThat(entity.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(entity.getStartTime()).isEqualTo(LocalTime.of(8, 0));
        assertThat(entity.getEndTime()).isEqualTo(LocalTime.of(12, 0));
    }
}
