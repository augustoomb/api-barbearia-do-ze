package com.augustoomb.api_barbearia_do_ze.domain.professional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProfessionalRepository extends JpaRepository<ProfessionalEntity, UUID> {

    List<ProfessionalEntity> findByEmailIgnoreCase(String email); // IgnoreCase: É um modificador que diz ao Spring para ignorar letras maiúsculas e minúsculas na hora da comparação.
}
