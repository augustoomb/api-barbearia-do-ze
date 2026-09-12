package com.augustoomb.api_barbearia_do_ze.application.professional;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProfessionalRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres")
        String name,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail deve possuir formato válido")
        @Size(max = 255, message = "E-mail deve ter no máximo 255 caracteres")
        String email
) {
}
