package com.augustoomb.api_barbearia_do_ze.application.availability;

import com.augustoomb.api_barbearia_do_ze.domain.availability.*;
import com.augustoomb.api_barbearia_do_ze.domain.professional.ProfessionalEntity;
import com.augustoomb.api_barbearia_do_ze.domain.professional.ProfessionalNotFoundException;
import com.augustoomb.api_barbearia_do_ze.domain.professional.ProfessionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private ProfessionalRepository professionalRepository;

    private final AvailabilityMapper availabilityMapper = Mappers.getMapper(AvailabilityMapper.class);

    private AvailabilityService availabilityService;

    @BeforeEach
    void setUp() {
        availabilityService = new AvailabilityService(availabilityRepository, professionalRepository, availabilityMapper);
    }

    @Test
    void shouldCreateAvailabilitySuccessfully() {
        UUID professionalId = UUID.randomUUID();
        ProfessionalEntity professional = buildProfessional(professionalId);
        CreateAvailabilityRequest request = new CreateAvailabilityRequest(1, "08:00", "12:00");

        when(professionalRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(availabilityRepository.findByProfessionalAndDayOfWeekOrderByStartTimeAsc(professional, DayOfWeek.MONDAY))
                .thenReturn(Collections.emptyList());
        when(availabilityRepository.save(any(AvailabilityEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AvailabilityResponse response = availabilityService.create(professionalId, request);

        assertThat(response.professionalId()).isEqualTo(professionalId);
        assertThat(response.dayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(response.startTime()).isEqualTo(LocalTime.of(8, 0));
        assertThat(response.endTime()).isEqualTo(LocalTime.of(12, 0));
        verify(availabilityRepository).save(any(AvailabilityEntity.class));
    }

    @Test
    void shouldRejectCreateWhenProfessionalNotFound() {
        UUID professionalId = UUID.randomUUID();
        CreateAvailabilityRequest request = new CreateAvailabilityRequest(1, "08:00", "12:00");

        when(professionalRepository.findById(professionalId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> availabilityService.create(professionalId, request))
                .isInstanceOf(ProfessionalNotFoundException.class);
    }

    @Test
    void shouldRejectCreateWhenEndTimeBeforeStartTime() {
        UUID professionalId = UUID.randomUUID();
        ProfessionalEntity professional = buildProfessional(professionalId);
        CreateAvailabilityRequest request = new CreateAvailabilityRequest(1, "14:00", "12:00");

        when(professionalRepository.findById(professionalId)).thenReturn(Optional.of(professional));

        assertThatThrownBy(() -> availabilityService.create(professionalId, request))
                .isInstanceOf(InvalidAvailabilityException.class)
                .hasMessageContaining("Horário de término deve ser posterior");
    }

    @Test
    void shouldRejectCreateWhenOverlapping() {
        UUID professionalId = UUID.randomUUID();
        ProfessionalEntity professional = buildProfessional(professionalId);
        CreateAvailabilityRequest request = new CreateAvailabilityRequest(1, "09:00", "13:00");

        AvailabilityEntity existing = buildAvailability(UUID.randomUUID(), professional, DayOfWeek.MONDAY, "08:00", "12:00");

        when(professionalRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(availabilityRepository.findByProfessionalAndDayOfWeekOrderByStartTimeAsc(professional, DayOfWeek.MONDAY))
                .thenReturn(List.of(existing));

        assertThatThrownBy(() -> availabilityService.create(professionalId, request))
                .isInstanceOf(OverlappingAvailabilityException.class);
    }

    @Test
    void shouldAllowConsecutivePeriods() {
        UUID professionalId = UUID.randomUUID();
        ProfessionalEntity professional = buildProfessional(professionalId);
        CreateAvailabilityRequest request = new CreateAvailabilityRequest(1, "12:00", "17:00");

        AvailabilityEntity existing = buildAvailability(UUID.randomUUID(), professional, DayOfWeek.MONDAY, "08:00", "12:00");

        when(professionalRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(availabilityRepository.findByProfessionalAndDayOfWeekOrderByStartTimeAsc(professional, DayOfWeek.MONDAY))
                .thenReturn(List.of(existing));
        when(availabilityRepository.save(any(AvailabilityEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AvailabilityResponse response = availabilityService.create(professionalId, request);

        assertThat(response.startTime()).isEqualTo(LocalTime.of(12, 0));
        assertThat(response.endTime()).isEqualTo(LocalTime.of(17, 0));
    }

    @Test
    void shouldFindAvailabilityByProfessionalOrdered() {
        UUID professionalId = UUID.randomUUID();
        ProfessionalEntity professional = buildProfessional(professionalId);

        AvailabilityEntity monday = buildAvailability(UUID.randomUUID(), professional, DayOfWeek.MONDAY, "08:00", "12:00");
        AvailabilityEntity tuesday = buildAvailability(UUID.randomUUID(), professional, DayOfWeek.TUESDAY, "13:00", "17:00");

        when(professionalRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(availabilityRepository.findByProfessionalOrderByDayOfWeekAscStartTimeAsc(professional))
                .thenReturn(List.of(monday, tuesday));

        List<AvailabilityResponse> responses = availabilityService.findByProfessional(professionalId, null);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).dayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(responses.get(1).dayOfWeek()).isEqualTo(DayOfWeek.TUESDAY);
    }

    @Test
    void shouldUpdateAvailabilitySuccessfully() {
        UUID professionalId = UUID.randomUUID();
        UUID availabilityId = UUID.randomUUID();
        ProfessionalEntity professional = buildProfessional(professionalId);
        AvailabilityEntity existing = buildAvailability(availabilityId, professional, DayOfWeek.MONDAY, "08:00", "12:00");
        UpdateAvailabilityRequest request = new UpdateAvailabilityRequest(2, "13:00", "17:00");

        when(professionalRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(existing));
        when(availabilityRepository.findByProfessionalAndDayOfWeekOrderByStartTimeAsc(professional, DayOfWeek.TUESDAY))
                .thenReturn(Collections.emptyList());
        when(availabilityRepository.save(any(AvailabilityEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AvailabilityResponse response = availabilityService.update(professionalId, availabilityId, request);

        assertThat(response.dayOfWeek()).isEqualTo(DayOfWeek.TUESDAY);
        assertThat(response.startTime()).isEqualTo(LocalTime.of(13, 0));
        assertThat(response.endTime()).isEqualTo(LocalTime.of(17, 0));
    }

    @Test
    void shouldRejectUpdateWhenAvailabilityNotFound() {
        UUID professionalId = UUID.randomUUID();
        UUID availabilityId = UUID.randomUUID();
        ProfessionalEntity professional = buildProfessional(professionalId);
        UpdateAvailabilityRequest request = new UpdateAvailabilityRequest(1, "08:00", "12:00");

        when(professionalRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> availabilityService.update(professionalId, availabilityId, request))
                .isInstanceOf(AvailabilityNotFoundException.class);
    }

    @Test
    void shouldRejectUpdateWhenAvailabilityBelongsToAnotherProfessional() {
        UUID professionalId = UUID.randomUUID();
        UUID otherProfessionalId = UUID.randomUUID();
        UUID availabilityId = UUID.randomUUID();
        ProfessionalEntity professional = buildProfessional(professionalId);
        ProfessionalEntity otherProfessional = buildProfessional(otherProfessionalId);
        AvailabilityEntity existing = buildAvailability(availabilityId, otherProfessional, DayOfWeek.MONDAY, "08:00", "12:00");
        UpdateAvailabilityRequest request = new UpdateAvailabilityRequest(1, "08:00", "12:00");

        when(professionalRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> availabilityService.update(professionalId, availabilityId, request))
                .isInstanceOf(AvailabilityNotFoundException.class);
    }

    @Test
    void shouldRejectUpdateWhenOverlapping() {
        UUID professionalId = UUID.randomUUID();
        UUID availabilityId = UUID.randomUUID();
        ProfessionalEntity professional = buildProfessional(professionalId);
        AvailabilityEntity existing = buildAvailability(availabilityId, professional, DayOfWeek.MONDAY, "08:00", "12:00");
        AvailabilityEntity other = buildAvailability(UUID.randomUUID(), professional, DayOfWeek.MONDAY, "13:00", "17:00");
        UpdateAvailabilityRequest request = new UpdateAvailabilityRequest(1, "12:00", "14:00");

        when(professionalRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(existing));
        when(availabilityRepository.findByProfessionalAndDayOfWeekOrderByStartTimeAsc(professional, DayOfWeek.MONDAY))
                .thenReturn(List.of(other));

        assertThatThrownBy(() -> availabilityService.update(professionalId, availabilityId, request))
                .isInstanceOf(OverlappingAvailabilityException.class);
    }

    @Test
    void shouldDeleteAvailabilitySuccessfully() {
        UUID professionalId = UUID.randomUUID();
        UUID availabilityId = UUID.randomUUID();
        ProfessionalEntity professional = buildProfessional(professionalId);
        AvailabilityEntity existing = buildAvailability(availabilityId, professional, DayOfWeek.MONDAY, "08:00", "12:00");

        when(professionalRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(existing));

        availabilityService.delete(professionalId, availabilityId);

        verify(availabilityRepository).delete(existing);
    }

    @Test
    void shouldRejectDeleteWhenAvailabilityNotFound() {
        UUID professionalId = UUID.randomUUID();
        UUID availabilityId = UUID.randomUUID();
        ProfessionalEntity professional = buildProfessional(professionalId);

        when(professionalRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> availabilityService.delete(professionalId, availabilityId))
                .isInstanceOf(AvailabilityNotFoundException.class);
    }

    @Test
    void shouldRejectDeleteWhenAvailabilityBelongsToAnotherProfessional() {
        UUID professionalId = UUID.randomUUID();
        UUID otherProfessionalId = UUID.randomUUID();
        UUID availabilityId = UUID.randomUUID();
        ProfessionalEntity professional = buildProfessional(professionalId);
        ProfessionalEntity otherProfessional = buildProfessional(otherProfessionalId);
        AvailabilityEntity existing = buildAvailability(availabilityId, otherProfessional, DayOfWeek.MONDAY, "08:00", "12:00");

        when(professionalRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> availabilityService.delete(professionalId, availabilityId))
                .isInstanceOf(AvailabilityNotFoundException.class);
    }

    private ProfessionalEntity buildProfessional(UUID id) {
        return ProfessionalEntity.builder()
                .id(id)
                .name("João")
                .email("joao@barbearia.com")
                .active(true)
                .build();
    }

    private AvailabilityEntity buildAvailability(UUID id, ProfessionalEntity professional, DayOfWeek dayOfWeek, String startTime, String endTime) {
        AvailabilityEntity entity = AvailabilityEntity.builder()
                .id(id)
                .professional(professional)
                .dayOfWeek(dayOfWeek)
                .startTime(LocalTime.parse(startTime))
                .endTime(LocalTime.parse(endTime))
                .build();
        return entity;
    }
}
