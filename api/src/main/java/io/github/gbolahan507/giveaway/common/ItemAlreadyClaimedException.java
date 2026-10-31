package io.github.gbolahan507.giveaway.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.CONFLICT)
public class ItemAlreadyClaimedException  extends RuntimeException{

    public ItemAlreadyClaimedException(UUID id) {
        super("Item is already claimed: " + id);
    }

}
