package ru.kikopark.localbackend.utils;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.Date;

@Getter
public class AppError {
    public static ResponseEntity<AppError> process(Object result) {
        if (result instanceof AppError appError) {
            return new ResponseEntity<>(appError, HttpStatusCode.valueOf((appError).getStatus()));
        } else {
            return new ResponseEntity<>(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected result"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private final int status;
    private final String message;
    private final Date timestamp;
    private final Object body;

    public AppError(int status, String message, Object body) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
        this.body = body;
    }

    public AppError(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
        this.body = null;
    }
}
