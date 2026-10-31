package io.github.gbolahan507.giveaway.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class CannotClaimOwnItemException extends  RuntimeException {

    public CannotClaimOwnItemException(UUID id) {
        super("You cannot claim your own item: " + id);
    }


}
