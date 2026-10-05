package com.augustoomb.api_barbearia_do_ze.application.availability;

import com.augustoomb.api_barbearia_do_ze.domain.availability.*;
import com.augustoomb.api_barbearia_do_ze.domain.professional.ProfessionalEntity;
import com.augustoomb.api_barbearia_do_ze.domain.professional.ProfessionalNotFoundException;
import com.augustoomb.api_barbearia_do_ze.domain.professional.ProfessionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final ProfessionalRepository professionalRepository;
    private final AvailabilityMapper availabilityMapper;

    @Transactional
    public AvailabilityResponse create(UUID professionalId, CreateAvailabilityRequest request) {
        ProfessionalEntity professional = findProfessional(professionalId);

        AvailabilityEntity entity = availabilityMapper.toEntity(request, professional);
        validateTimeRange(entity.getStartTime(), entity.getEndTime());
        validateNoOverlap(entity);

        AvailabilityEntity saved = availabilityRepository.save(entity);
        return availabilityMapper.toResponse(saved);
    }

    public List<AvailabilityResponse> findByProfessional(UUID professionalId, Integer dayOfWeek) {
        ProfessionalEntity professional = findProfessional(professionalId);

        List<AvailabilityEntity> availabilities;
        if (dayOfWeek != null) {
            availabilities = availabilityRepository.findByProfessionalAndDayOfWeekOrderByDayOfWeekAscStartTimeAsc(
                    professional, DayOfWeek.of(dayOfWeek));
        } else {
            availabilities = availabilityRepository.findByProfessionalOrderByDayOfWeekAscStartTimeAsc(professional);
        }

        return availabilityMapper.toResponseList(availabilities);
    }

    @Transactional
    public AvailabilityResponse update(UUID professionalId, UUID availabilityId, UpdateAvailabilityRequest request) {
        ProfessionalEntity professional = findProfessional(professionalId);
        AvailabilityEntity entity = findAvailability(availabilityId);

        validateAvailabilityBelongsToProfessional(entity, professional);

        availabilityMapper.updateEntityFromRequest(request, entity);
        validateTimeRange(entity.getStartTime(), entity.getEndTime());
        validateNoOverlap(entity);

        AvailabilityEntity updated = availabilityRepository.save(entity);
        return availabilityMapper.toResponse(updated);
    }

    @Transactional
    public void delete(UUID professionalId, UUID availabilityId) {
        ProfessionalEntity professional = findProfessional(professionalId);
        AvailabilityEntity entity = findAvailability(availabilityId);

        validateAvailabilityBelongsToProfessional(entity, professional);

        availabilityRepository.delete(entity);
    }

    private ProfessionalEntity findProfessional(UUID professionalId) {
        return professionalRepository.findById(professionalId)
                .orElseThrow(ProfessionalNotFoundException::new);
    }

    private AvailabilityEntity findAvailability(UUID availabilityId) {
        return availabilityRepository.findById(availabilityId)
                .orElseThrow(AvailabilityNotFoundException::new);
    }

    private void validateAvailabilityBelongsToProfessional(AvailabilityEntity entity, ProfessionalEntity professional) {
        if (!entity.getProfessional().getId().equals(professional.getId())) {
            throw new AvailabilityNotFoundException();
        }
    }

    private void validateTimeRange(LocalTime startTime, LocalTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new InvalidAvailabilityException("Horário de término deve ser posterior ao horário de início");
        }
    }

    private void validateNoOverlap(AvailabilityEntity entity) {
        DayOfWeek dayOfWeek = entity.getDayOfWeek();
        LocalTime startTime = entity.getStartTime();
        LocalTime endTime = entity.getEndTime();
        UUID currentId = entity.getId();

        List<AvailabilityEntity> sameDayPeriods = availabilityRepository
                .findByProfessionalAndDayOfWeekOrderByStartTimeAsc(entity.getProfessional(), dayOfWeek);

        boolean hasOverlap = sameDayPeriods.stream()
                .filter(existing -> currentId == null || !existing.getId().equals(currentId))
                .anyMatch(existing ->
                        startTime.isBefore(existing.getEndTime()) && endTime.isAfter(existing.getStartTime())
                );

        if (hasOverlap) {
            throw new OverlappingAvailabilityException();
        }
    }
}
