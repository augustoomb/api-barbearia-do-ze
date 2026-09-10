package com.augustoomb.api_barbearia_do_ze.domain.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, UUID> {

    List<ServiceEntity> findAllByActiveTrue();

    Optional<ServiceEntity> findByNameIgnoreCaseAndActiveTrue(String name);
}
