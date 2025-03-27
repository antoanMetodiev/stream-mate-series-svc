package com.example.streammateseriessvc.app.exception;

public class CommentNotFoundException extends RuntimeException {

    public CommentNotFoundException(String message) {
        super(message);
    }

    public CommentNotFoundException() {
    }
}
