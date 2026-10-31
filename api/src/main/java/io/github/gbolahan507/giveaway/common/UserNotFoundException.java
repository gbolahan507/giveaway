package io.github.gbolahan507.giveaway.common;

import org.springframework.web.bind.annotation.ResponseStatus;



import org.springframework.http.HttpStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID id) {
        super("No user with id " + id);
    }
}