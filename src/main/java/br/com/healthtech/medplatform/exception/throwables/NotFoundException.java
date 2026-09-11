package br.com.healthtech.medplatform.exception.throwables;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
