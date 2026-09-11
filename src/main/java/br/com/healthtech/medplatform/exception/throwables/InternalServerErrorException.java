package br.com.healthtech.medplatform.exception.throwables;

public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(String message) {
        super(message);
    }
}
