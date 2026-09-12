package com.augustoomb.api_barbearia_do_ze.application.professional;

import com.augustoomb.api_barbearia_do_ze.domain.professional.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;
    private final ProfessionalMapper professionalMapper;

    @Transactional
    public ProfessionalResponse create(CreateProfessionalRequest request) {
        validateName(request.name());
        validateEmail(request.email(), null);

        ProfessionalEntity entity = professionalMapper.toEntity(request);
        entity.setActive(true);

        ProfessionalEntity saved = professionalRepository.save(entity);
        return professionalMapper.toResponse(saved);
    }

    public List<ProfessionalResponse> findAll() {
        return professionalMapper.toResponseList(professionalRepository.findAll());
    }

    public ProfessionalResponse findById(UUID id) {
        ProfessionalEntity entity = professionalRepository.findById(id)
                .orElseThrow(ProfessionalNotFoundException::new);
        return professionalMapper.toResponse(entity);
    }

    @Transactional
    public ProfessionalResponse update(UUID id, UpdateProfessionalRequest request) {
        if (isBlankRequest(request)) {
            throw new InvalidProfessionalException("Nenhum campo informado para atualização");
        }

        ProfessionalEntity entity = professionalRepository.findById(id)
                .orElseThrow(ProfessionalNotFoundException::new);

        if (request.name() != null) {
            validateName(request.name());
        }

        if (request.email() != null) {
            validateEmail(request.email(), entity.getId());
        }

        professionalMapper.updateEntityFromRequest(request, entity);
        ProfessionalEntity updated = professionalRepository.save(entity);
        return professionalMapper.toResponse(updated);
    }

    @Transactional
    public ProfessionalResponse activate(UUID id) {
        ProfessionalEntity entity = professionalRepository.findById(id)
                .orElseThrow(ProfessionalNotFoundException::new);

        if (entity.isActive()) {
            return professionalMapper.toResponse(entity);
        }

        validateEmail(entity.getEmail(), entity.getId());
        entity.setActive(true);
        ProfessionalEntity updated = professionalRepository.save(entity);
        return professionalMapper.toResponse(updated);
    }

    @Transactional
    public ProfessionalResponse deactivate(UUID id) {
        ProfessionalEntity entity = professionalRepository.findById(id)
                .orElseThrow(ProfessionalNotFoundException::new);

        if (!entity.isActive()) {
            return professionalMapper.toResponse(entity);
        }

        entity.setActive(false);
        ProfessionalEntity updated = professionalRepository.save(entity);
        return professionalMapper.toResponse(updated);
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidProfessionalException("Nome é obrigatório");
        }
    }

    private void validateEmail(String email, UUID currentId) {
        String normalized = EmailNormalizer.normalize(email);
        if (normalized == null || normalized.isBlank()) {
            throw new InvalidProfessionalException("E-mail é obrigatório");
        }

        boolean hasConflict = professionalRepository.findByEmailIgnoreCase(email).stream()
                .anyMatch(existing -> currentId == null || !currentId.equals(existing.getId()));

        if (hasConflict) {
            throw new DuplicateProfessionalEmailException();
        }
    }

    private boolean isBlankRequest(UpdateProfessionalRequest request) {
        return request.name() == null && request.email() == null;
    }
}
