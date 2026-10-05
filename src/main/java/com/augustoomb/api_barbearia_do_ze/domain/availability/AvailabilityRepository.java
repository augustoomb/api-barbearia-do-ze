package com.augustoomb.api_barbearia_do_ze.domain.availability;

import com.augustoomb.api_barbearia_do_ze.domain.professional.ProfessionalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@Repository
public interface AvailabilityRepository extends JpaRepository<AvailabilityEntity, UUID> {

    List<AvailabilityEntity> findByProfessionalOrderByDayOfWeekAscStartTimeAsc(ProfessionalEntity professional);

    List<AvailabilityEntity> findByProfessionalAndDayOfWeekOrderByStartTimeAsc(ProfessionalEntity professional, DayOfWeek dayOfWeek);

    List<AvailabilityEntity> findByProfessionalAndDayOfWeekOrderByDayOfWeekAscStartTimeAsc(ProfessionalEntity professional, DayOfWeek dayOfWeek);
}
