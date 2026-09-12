package com.augustoomb.api_barbearia_do_ze.application.professional;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProfessionalResponse(
        UUID id,
        String name,
        String email,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
