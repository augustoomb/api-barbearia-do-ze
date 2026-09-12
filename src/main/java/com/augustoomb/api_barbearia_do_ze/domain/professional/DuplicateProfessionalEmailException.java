package com.augustoomb.api_barbearia_do_ze.domain.professional;

public class DuplicateProfessionalEmailException extends RuntimeException {

    public DuplicateProfessionalEmailException() {
        super("Já existe um profissional com este e-mail");
    }
}
