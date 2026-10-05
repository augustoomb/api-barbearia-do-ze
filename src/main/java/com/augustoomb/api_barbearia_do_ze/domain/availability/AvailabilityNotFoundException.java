package com.augustoomb.api_barbearia_do_ze.domain.availability;

public class AvailabilityNotFoundException extends RuntimeException {

    public static final String CODE = "AVAILABILITY_NOT_FOUND";

    public AvailabilityNotFoundException() {
        super("Período de disponibilidade não encontrado");
    }

    public String getCode() {
        return CODE;
    }
}
