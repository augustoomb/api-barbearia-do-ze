package com.augustoomb.api_barbearia_do_ze.domain.availability;

public class InvalidAvailabilityException extends RuntimeException {

    public static final String CODE = "INVALID_AVAILABILITY_PERIOD";

    public InvalidAvailabilityException(String message) {
        super(message);
    }

    public String getCode() {
        return CODE;
    }
}
