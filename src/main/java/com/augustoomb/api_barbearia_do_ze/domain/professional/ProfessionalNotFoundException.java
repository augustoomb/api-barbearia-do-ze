package com.augustoomb.api_barbearia_do_ze.domain.professional;

public class ProfessionalNotFoundException extends RuntimeException {

    public ProfessionalNotFoundException() {
        super("Profissional não encontrado");
    }
}
