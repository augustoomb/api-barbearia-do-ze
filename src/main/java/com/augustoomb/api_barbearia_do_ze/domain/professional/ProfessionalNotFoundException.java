package com.augustoomb.api_barbearia_do_ze.domain.professional;

public class ProfessionalNotFoundException extends RuntimeException {

    public static final String CODE = "PROFESSIONAL_NOT_FOUND";

    public ProfessionalNotFoundException() {
        super("Profissional não encontrado");
    }

    public String getCode() {
        return CODE;
    }
}
