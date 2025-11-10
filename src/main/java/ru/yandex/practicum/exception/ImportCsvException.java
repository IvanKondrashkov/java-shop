package ru.yandex.practicum.exception;

public class ImportCsvException extends RuntimeException {
    public ImportCsvException(String message) {
        super(message);
    }

    public ImportCsvException(String message, Throwable cause) {
        super(message, cause);
    }
}