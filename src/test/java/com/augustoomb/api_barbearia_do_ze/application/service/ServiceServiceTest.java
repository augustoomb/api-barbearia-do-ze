package com.augustoomb.api_barbearia_do_ze.application.service;

import com.augustoomb.api_barbearia_do_ze.domain.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    private final ServiceMapper serviceMapper = Mappers.getMapper(ServiceMapper.class);

    private ServiceService serviceService;

    @BeforeEach
    void setUp() {
        serviceService = new ServiceService(serviceRepository, serviceMapper);
    }

    @Test
    void shouldCreateServiceSuccessfully() {
        CreateServiceRequest request = CreateServiceRequest.builder()
                .name("Corte")
                .description("Corte social")
                .durationMinutes(30)
                .price(new BigDecimal("35.00"))
                .build();

        when(serviceRepository.findByNameIgnoreCaseAndActiveTrue("Corte")).thenReturn(Optional.empty());
        when(serviceRepository.save(any(ServiceEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceResponse response = serviceService.create(request);

        assertThat(response.name()).isEqualTo("Corte");
        assertThat(response.active()).isTrue();
        verify(serviceRepository).save(any(ServiceEntity.class));
    }

    @Test
    void shouldRejectBlankNameOnCreate() {
        CreateServiceRequest request = CreateServiceRequest.builder()
                .name("   ")
                .durationMinutes(30)
                .price(new BigDecimal("35.00"))
                .build();

        assertThatThrownBy(() -> serviceService.create(request))
                .isInstanceOf(InvalidServiceException.class)
                .hasMessageContaining("Nome é obrigatório");
    }

    @Test
    void shouldRejectDuplicateNormalizedNameOnCreate() {
        CreateServiceRequest request = CreateServiceRequest.builder()
                .name("Corte")
                .durationMinutes(30)
                .price(new BigDecimal("35.00"))
                .build();

        ServiceEntity existing = ServiceEntity.builder()
                .id(UUID.randomUUID())
                .name("corte")
                .durationMinutes(30)
                .price(new BigDecimal("35.00"))
                .active(true)
                .build();

        when(serviceRepository.findByNameIgnoreCaseAndActiveTrue("Corte")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> serviceService.create(request))
                .isInstanceOf(DuplicateServiceNameException.class);
    }

    @Test
    void shouldFindAllServices() {
        ServiceEntity service = ServiceEntity.builder()
                .id(UUID.randomUUID())
                .name("Corte")
                .durationMinutes(30)
                .price(new BigDecimal("35.00"))
                .active(true)
                .build();

        when(serviceRepository.findAll()).thenReturn(List.of(service));

        List<ServiceResponse> responses = serviceService.findAll();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).name()).isEqualTo("Corte");
    }

    @Test
    void shouldFindServiceById() {
        UUID id = UUID.randomUUID();
        ServiceEntity service = ServiceEntity.builder()
                .id(id)
                .name("Corte")
                .durationMinutes(30)
                .price(new BigDecimal("35.00"))
                .active(true)
                .build();

        when(serviceRepository.findById(id)).thenReturn(Optional.of(service));

        ServiceResponse response = serviceService.findById(id);

        assertThat(response.id()).isEqualTo(id);
    }

    @Test
    void shouldThrowNotFoundWhenServiceDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(serviceRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serviceService.findById(id))
                .isInstanceOf(ServiceNotFoundException.class);
    }

    @Test
    void shouldUpdateServicePartially() {
        UUID id = UUID.randomUUID();
        ServiceEntity existing = ServiceEntity.builder()
                .id(id)
                .name("Corte")
                .description("Antiga")
                .durationMinutes(30)
                .price(new BigDecimal("35.00"))
                .active(true)
                .build();

        UpdateServiceRequest request = UpdateServiceRequest.builder()
                .price(new BigDecimal("40.00"))
                .build();

        when(serviceRepository.findById(id)).thenReturn(Optional.of(existing));
        when(serviceRepository.save(any(ServiceEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceResponse response = serviceService.update(id, request);

        assertThat(response.price()).isEqualByComparingTo(new BigDecimal("40.00"));
        assertThat(response.name()).isEqualTo("Corte");
        assertThat(response.durationMinutes()).isEqualTo(30);
    }

    @Test
    void shouldRejectEmptyUpdateRequest() {
        UUID id = UUID.randomUUID();
        UpdateServiceRequest request = UpdateServiceRequest.builder().build();

        assertThatThrownBy(() -> serviceService.update(id, request))
                .isInstanceOf(InvalidServiceException.class)
                .hasMessageContaining("Nenhum campo informado");
    }

    @Test
    void shouldActivateService() {
        UUID id = UUID.randomUUID();
        ServiceEntity inactive = ServiceEntity.builder()
                .id(id)
                .name("Corte")
                .durationMinutes(30)
                .price(new BigDecimal("35.00"))
                .active(false)
                .build();

        when(serviceRepository.findById(id)).thenReturn(Optional.of(inactive));
        when(serviceRepository.findByNameIgnoreCaseAndActiveTrue("Corte")).thenReturn(Optional.empty());
        when(serviceRepository.save(any(ServiceEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceResponse response = serviceService.activate(id);

        assertThat(response.active()).isTrue();
    }

    @Test
    void shouldDeactivateService() {
        UUID id = UUID.randomUUID();
        ServiceEntity active = ServiceEntity.builder()
                .id(id)
                .name("Corte")
                .durationMinutes(30)
                .price(new BigDecimal("35.00"))
                .active(true)
                .build();

        when(serviceRepository.findById(id)).thenReturn(Optional.of(active));
        when(serviceRepository.save(any(ServiceEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceResponse response = serviceService.deactivate(id);

        assertThat(response.active()).isFalse();
    }

    @Test
    void shouldBeIdempotentOnActivation() {
        UUID id = UUID.randomUUID();
        ServiceEntity active = ServiceEntity.builder()
                .id(id)
                .name("Corte")
                .durationMinutes(30)
                .price(new BigDecimal("35.00"))
                .active(true)
                .build();

        when(serviceRepository.findById(id)).thenReturn(Optional.of(active));

        ServiceResponse response = serviceService.activate(id);

        assertThat(response.active()).isTrue();
        verify(serviceRepository, never()).save(any(ServiceEntity.class));
    }
}
