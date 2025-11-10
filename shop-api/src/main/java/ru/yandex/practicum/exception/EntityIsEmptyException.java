package ru.yandex.practicum.exception;

public class EntityIsEmptyException extends RuntimeException {
    public EntityIsEmptyException(String message) {
        super(message);
    }

    public EntityIsEmptyException(String message, Throwable cause) {
        super(message, cause);
    }
}