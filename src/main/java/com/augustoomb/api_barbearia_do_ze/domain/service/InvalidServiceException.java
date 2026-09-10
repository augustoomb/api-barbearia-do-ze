package com.augustoomb.api_barbearia_do_ze.domain.service;

public class InvalidServiceException extends RuntimeException {

    public InvalidServiceException(String message) {
        super(message);
    }
}
