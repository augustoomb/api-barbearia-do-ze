package com.augustoomb.api_barbearia_do_ze.domain.availability;

public class OverlappingAvailabilityException extends RuntimeException {

    public static final String CODE = "OVERLAPPING_AVAILABILITY";

    public OverlappingAvailabilityException() {
        super("O período informado conflita com outro período de disponibilidade do profissional");
    }

    public String getCode() {
        return CODE;
    }
}
