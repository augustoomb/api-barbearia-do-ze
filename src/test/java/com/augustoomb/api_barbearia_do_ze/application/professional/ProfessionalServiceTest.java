package com.augustoomb.api_barbearia_do_ze.application.professional;

import com.augustoomb.api_barbearia_do_ze.domain.professional.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfessionalServiceTest {

    @Mock
    private ProfessionalRepository professionalRepository;

    private final ProfessionalMapper professionalMapper = Mappers.getMapper(ProfessionalMapper.class);

    private ProfessionalService professionalService;

    @BeforeEach
    void setUp() {
        professionalService = new ProfessionalService(professionalRepository, professionalMapper);
    }

    @Test
    void shouldCreateProfessionalSuccessfully() {
        CreateProfessionalRequest request = new CreateProfessionalRequest("João Silva", "joao@barbearia.com");

        when(professionalRepository.findByEmailIgnoreCase("joao@barbearia.com")).thenReturn(Collections.emptyList());
        when(professionalRepository.save(any(ProfessionalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfessionalResponse response = professionalService.create(request);

        assertThat(response.name()).isEqualTo("João Silva");
        assertThat(response.email()).isEqualTo("joao@barbearia.com");
        assertThat(response.active()).isTrue();
        verify(professionalRepository).save(any(ProfessionalEntity.class));
    }

    @Test
    void shouldRejectBlankNameOnCreate() {
        CreateProfessionalRequest request = new CreateProfessionalRequest("   ", "joao@barbearia.com");

        assertThatThrownBy(() -> professionalService.create(request))
                .isInstanceOf(InvalidProfessionalException.class)
                .hasMessageContaining("Nome é obrigatório");
    }

    @Test
    void shouldRejectBlankEmailOnCreate() {
        CreateProfessionalRequest request = new CreateProfessionalRequest("João Silva", "   ");

        assertThatThrownBy(() -> professionalService.create(request))
                .isInstanceOf(InvalidProfessionalException.class)
                .hasMessageContaining("E-mail é obrigatório");
    }

    @Test
    void shouldRejectDuplicateNormalizedEmailOnCreate() {
        CreateProfessionalRequest request = new CreateProfessionalRequest("João Silva", "joao@barbearia.com");

        ProfessionalEntity existing = ProfessionalEntity.builder()
                .id(UUID.randomUUID())
                .name("Outro")
                .email("JOAO@barbearia.com")
                .active(true)
                .build();

        when(professionalRepository.findByEmailIgnoreCase("joao@barbearia.com")).thenReturn(List.of(existing));

        assertThatThrownBy(() -> professionalService.create(request))
                .isInstanceOf(DuplicateProfessionalEmailException.class);
    }

    @Test
    void shouldFindAllProfessionals() {
        ProfessionalEntity professional = ProfessionalEntity.builder()
                .id(UUID.randomUUID())
                .name("João Silva")
                .email("joao@barbearia.com")
                .active(true)
                .build();

        when(professionalRepository.findAll()).thenReturn(List.of(professional));

        List<ProfessionalResponse> responses = professionalService.findAll();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).name()).isEqualTo("João Silva");
    }

    @Test
    void shouldFindProfessionalById() {
        UUID id = UUID.randomUUID();
        ProfessionalEntity professional = ProfessionalEntity.builder()
                .id(id)
                .name("João Silva")
                .email("joao@barbearia.com")
                .active(true)
                .build();

        when(professionalRepository.findById(id)).thenReturn(Optional.of(professional));

        ProfessionalResponse response = professionalService.findById(id);

        assertThat(response.id()).isEqualTo(id);
    }

    @Test
    void shouldThrowNotFoundWhenProfessionalDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(professionalRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professionalService.findById(id))
                .isInstanceOf(ProfessionalNotFoundException.class);
    }

    @Test
    void shouldUpdateProfessionalPartially() {
        UUID id = UUID.randomUUID();
        ProfessionalEntity existing = ProfessionalEntity.builder()
                .id(id)
                .name("João Silva")
                .email("joao@barbearia.com")
                .active(true)
                .build();

        UpdateProfessionalRequest request = new UpdateProfessionalRequest(null, "joao.novo@barbearia.com");

        when(professionalRepository.findById(id)).thenReturn(Optional.of(existing));
        when(professionalRepository.findByEmailIgnoreCase("joao.novo@barbearia.com")).thenReturn(Collections.emptyList());
        when(professionalRepository.save(any(ProfessionalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfessionalResponse response = professionalService.update(id, request);

        assertThat(response.email()).isEqualTo("joao.novo@barbearia.com");
        assertThat(response.name()).isEqualTo("João Silva");
    }

    @Test
    void shouldRejectEmptyUpdateRequest() {
        UUID id = UUID.randomUUID();
        UpdateProfessionalRequest request = new UpdateProfessionalRequest(null, null);

        assertThatThrownBy(() -> professionalService.update(id, request))
                .isInstanceOf(InvalidProfessionalException.class)
                .hasMessageContaining("Nenhum campo informado");
    }

    @Test
    void shouldRejectUpdateToDuplicateEmail() {
        UUID id = UUID.randomUUID();
        ProfessionalEntity existing = ProfessionalEntity.builder()
                .id(id)
                .name("João Silva")
                .email("joao@barbearia.com")
                .active(true)
                .build();

        ProfessionalEntity other = ProfessionalEntity.builder()
                .id(UUID.randomUUID())
                .name("Outro")
                .email("outro@barbearia.com")
                .active(true)
                .build();

        UpdateProfessionalRequest request = new UpdateProfessionalRequest(null, "outro@barbearia.com");

        when(professionalRepository.findById(id)).thenReturn(Optional.of(existing));
        when(professionalRepository.findByEmailIgnoreCase("outro@barbearia.com")).thenReturn(List.of(other));

        assertThatThrownBy(() -> professionalService.update(id, request))
                .isInstanceOf(DuplicateProfessionalEmailException.class);
    }

    @Test
    void shouldActivateProfessional() {
        UUID id = UUID.randomUUID();
        ProfessionalEntity inactive = ProfessionalEntity.builder()
                .id(id)
                .name("João Silva")
                .email("joao@barbearia.com")
                .active(false)
                .build();

        when(professionalRepository.findById(id)).thenReturn(Optional.of(inactive));
        when(professionalRepository.findByEmailIgnoreCase("joao@barbearia.com")).thenReturn(Collections.emptyList());
        when(professionalRepository.save(any(ProfessionalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfessionalResponse response = professionalService.activate(id);

        assertThat(response.active()).isTrue();
    }

    @Test
    void shouldDeactivateProfessional() {
        UUID id = UUID.randomUUID();
        ProfessionalEntity active = ProfessionalEntity.builder()
                .id(id)
                .name("João Silva")
                .email("joao@barbearia.com")
                .active(true)
                .build();

        when(professionalRepository.findById(id)).thenReturn(Optional.of(active));
        when(professionalRepository.save(any(ProfessionalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfessionalResponse response = professionalService.deactivate(id);

        assertThat(response.active()).isFalse();
    }

    @Test
    void shouldBeIdempotentOnActivation() {
        UUID id = UUID.randomUUID();
        ProfessionalEntity active = ProfessionalEntity.builder()
                .id(id)
                .name("João Silva")
                .email("joao@barbearia.com")
                .active(true)
                .build();

        when(professionalRepository.findById(id)).thenReturn(Optional.of(active));

        ProfessionalResponse response = professionalService.activate(id);

        assertThat(response.active()).isTrue();
        verify(professionalRepository, never()).save(any(ProfessionalEntity.class));
    }

    @Test
    void shouldBeIdempotentOnDeactivation() {
        UUID id = UUID.randomUUID();
        ProfessionalEntity inactive = ProfessionalEntity.builder()
                .id(id)
                .name("João Silva")
                .email("joao@barbearia.com")
                .active(false)
                .build();

        when(professionalRepository.findById(id)).thenReturn(Optional.of(inactive));

        ProfessionalResponse response = professionalService.deactivate(id);

        assertThat(response.active()).isFalse();
        verify(professionalRepository, never()).save(any(ProfessionalEntity.class));
    }

    @Test
    void shouldRejectActivationWhenEmailConflictsWithAnotherProfessional() {
        UUID id = UUID.randomUUID();
        ProfessionalEntity inactive = ProfessionalEntity.builder()
                .id(id)
                .name("João Silva")
                .email("joao@barbearia.com")
                .active(false)
                .build();

        ProfessionalEntity other = ProfessionalEntity.builder()
                .id(UUID.randomUUID())
                .name("Outro João")
                .email("joao@barbearia.com")
                .active(true)
                .build();

        when(professionalRepository.findById(id)).thenReturn(Optional.of(inactive));
        when(professionalRepository.findByEmailIgnoreCase("joao@barbearia.com")).thenReturn(List.of(other));

        assertThatThrownBy(() -> professionalService.activate(id))
                .isInstanceOf(DuplicateProfessionalEmailException.class);
    }
}
