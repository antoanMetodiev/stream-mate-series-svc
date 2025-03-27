package com.example.streammateseriessvc.app.exception;

public class SeriesNotFoundException extends RuntimeException {

    public SeriesNotFoundException(String message) {
        super(message);
    }

    public SeriesNotFoundException() {
    }
}
