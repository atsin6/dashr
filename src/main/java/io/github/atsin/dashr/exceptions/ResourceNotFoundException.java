package io.github.atsin.dashr.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {

    }
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
