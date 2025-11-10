package ru.yandex.practicum.exception;

public record ErrorResponse(String error,
                            int statusCode) {
}