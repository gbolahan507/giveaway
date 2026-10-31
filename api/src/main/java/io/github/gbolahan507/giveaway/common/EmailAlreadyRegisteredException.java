package io.github.gbolahan507.giveaway.common;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyRegisteredException extends  RuntimeException{
    public EmailAlreadyRegisteredException(String email) {
        super("Email already registered: " + email);
    }
}
