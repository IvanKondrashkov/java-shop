package ru.yandex.practicum.exception;

public class S3ConnectionException extends RuntimeException {
    public S3ConnectionException(String message) {
        super(message);
    }

    public S3ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}