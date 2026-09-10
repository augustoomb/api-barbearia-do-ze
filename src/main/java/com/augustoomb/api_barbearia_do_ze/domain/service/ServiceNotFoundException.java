package com.augustoomb.api_barbearia_do_ze.domain.service;

public class ServiceNotFoundException extends RuntimeException {

    public ServiceNotFoundException() {
        super("Serviço não encontrado");
    }
}
