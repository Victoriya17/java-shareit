package ru.practicum.shareit.exceptions;

public class NotItemOwnerException extends RuntimeException {
    public NotItemOwnerException(String message) {
        super(message);
    }
}
