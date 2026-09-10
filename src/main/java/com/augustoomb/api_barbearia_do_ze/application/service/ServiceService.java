package com.augustoomb.api_barbearia_do_ze.application.service;

import com.augustoomb.api_barbearia_do_ze.domain.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;

    @Transactional
    public ServiceResponse create(CreateServiceRequest request) {
        validateName(request.name(), null);
        ServiceEntity entity = serviceMapper.toEntity(request);
        entity.setActive(true);
        ServiceEntity saved = serviceRepository.save(entity);
        return serviceMapper.toResponse(saved);
    }

    public List<ServiceResponse> findAll() {
        return serviceMapper.toResponseList(serviceRepository.findAll());
    }

    public List<ServiceResponse> findAllActive() {
        return serviceMapper.toResponseList(serviceRepository.findAllByActiveTrue());
    }

    public ServiceResponse findById(UUID id) {
        ServiceEntity entity = serviceRepository.findById(id)
                .orElseThrow(ServiceNotFoundException::new);
        return serviceMapper.toResponse(entity);
    }

    @Transactional
    public ServiceResponse update(UUID id, UpdateServiceRequest request) {
        if (isBlankRequest(request)) {
            throw new InvalidServiceException("Nenhum campo informado para atualização");
        }

        ServiceEntity entity = serviceRepository.findById(id)
                .orElseThrow(ServiceNotFoundException::new);

        if (request.name() != null) {
            validateName(request.name(), entity.getId());
        }

        serviceMapper.updateEntityFromRequest(request, entity);
        ServiceEntity updated = serviceRepository.save(entity);
        return serviceMapper.toResponse(updated);
    }

    @Transactional
    public ServiceResponse activate(UUID id) {
        ServiceEntity entity = serviceRepository.findById(id)
                .orElseThrow(ServiceNotFoundException::new);

        if (entity.isActive()) {
            return serviceMapper.toResponse(entity);
        }

        validateName(entity.getName(), entity.getId());
        entity.setActive(true);
        ServiceEntity updated = serviceRepository.save(entity);
        return serviceMapper.toResponse(updated);
    }

    @Transactional
    public ServiceResponse deactivate(UUID id) {
        ServiceEntity entity = serviceRepository.findById(id)
                .orElseThrow(ServiceNotFoundException::new);

        if (!entity.isActive()) {
            return serviceMapper.toResponse(entity);
        }

        entity.setActive(false);
        ServiceEntity updated = serviceRepository.save(entity);
        return serviceMapper.toResponse(updated);
    }

    private void validateName(String name, UUID currentId) {
        String normalized = NameNormalizer.normalize(name);
        if (normalized == null || normalized.isBlank()) {
            throw new InvalidServiceException("Nome é obrigatório");
        }

        serviceRepository.findByNameIgnoreCaseAndActiveTrue(name)
                .ifPresent(existing -> {
                    if (currentId == null || !currentId.equals(existing.getId())) {
                        throw new DuplicateServiceNameException();
                    }
                });
    }

    private boolean isBlankRequest(UpdateServiceRequest request) {
        return request.name() == null
                && request.description() == null
                && request.durationMinutes() == null
                && request.price() == null;
    }
}
