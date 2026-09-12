package com.augustoomb.api_barbearia_do_ze.application.professional;

import com.augustoomb.api_barbearia_do_ze.domain.professional.ProfessionalEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProfessionalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProfessionalEntity toEntity(CreateProfessionalRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateProfessionalRequest request, @MappingTarget ProfessionalEntity entity);

    ProfessionalResponse toResponse(ProfessionalEntity entity);

    List<ProfessionalResponse> toResponseList(List<ProfessionalEntity> entities);
}
