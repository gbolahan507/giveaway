package io.github.gbolahan507.giveaway.item;

import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService){

        this.itemService = itemService;

    }

    @PostMapping
    public ResponseEntity<ItemDetail> post(
            @RequestHeader("Giveaway-User-Id") UUID userId, @Valid @RequestBody CreateItemRequest request ) {

        ItemDetail created = itemService.post(request, userId);
        return ResponseEntity.created(URI.create("/items/L" + created.id())).body(created);
    }


    @GetMapping("/{id}")
    public ItemDetail get(
            @RequestHeader("Giveaway-User-Id") UUID userId, @PathVariable UUID id) {
        return  itemService.detail(id, userId);

    }

    @PostMapping("/{id}/claim")
    public ItemDetail claim(@RequestHeader("Giveaway-User-Id") UUID userId, @PathVariable UUID id) {
        return  itemService.claim(id, userId);
    }


}
