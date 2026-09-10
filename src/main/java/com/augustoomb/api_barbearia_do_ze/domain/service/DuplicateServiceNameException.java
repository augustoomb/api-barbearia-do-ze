package com.augustoomb.api_barbearia_do_ze.domain.service;

public class DuplicateServiceNameException extends RuntimeException {

    public DuplicateServiceNameException() {
        super("Já existe um serviço ativo com este nome");
    }
}
