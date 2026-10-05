package com.augustoomb.api_barbearia_do_ze.application.availability;

import com.augustoomb.api_barbearia_do_ze.domain.availability.AvailabilityEntity;
import com.augustoomb.api_barbearia_do_ze.domain.professional.ProfessionalEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AvailabilityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "professional", source = "professional")
    @Mapping(target = "dayOfWeek", source = "request.dayOfWeek")
    @Mapping(target = "startTime", source = "request.startTime")
    @Mapping(target = "endTime", source = "request.endTime")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AvailabilityEntity toEntity(CreateAvailabilityRequest request, ProfessionalEntity professional);

    @Mapping(target = "professionalId", source = "professional.id")
    AvailabilityResponse toResponse(AvailabilityEntity entity);

    List<AvailabilityResponse> toResponseList(List<AvailabilityEntity> entities);

    @Mapping(target = "dayOfWeek", source = "dayOfWeek")
    @Mapping(target = "startTime", source = "startTime")
    @Mapping(target = "endTime", source = "endTime")
    void updateEntityFromRequest(UpdateAvailabilityRequest request, @MappingTarget AvailabilityEntity entity);

    default DayOfWeek map(Integer dayOfWeek) {
        if (dayOfWeek == null) {
            return null;
        }
        return DayOfWeek.of(dayOfWeek);
    }

    default LocalTime map(String time) {
        if (time == null) {
            return null;
        }
        return LocalTime.parse(time);
    }
}
