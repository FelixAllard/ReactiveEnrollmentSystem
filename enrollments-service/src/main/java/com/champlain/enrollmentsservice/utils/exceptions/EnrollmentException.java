package com.champlain.enrollmentsservice.utils.exceptions;

public class EnrollmentException extends RuntimeException {
    public EnrollmentException(String message) {
        super(message);
    }

    public EnrollmentException(String message, Throwable cause) {
        super(message, cause);
    }

    public EnrollmentException(Throwable cause) {
        super(cause);
    }
}