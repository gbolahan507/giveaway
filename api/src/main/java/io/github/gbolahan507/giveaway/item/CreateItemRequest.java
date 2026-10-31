package io.github.gbolahan507.giveaway.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateItemRequest (

    @NotBlank @Size(max = 200) String title,
    @Size(max = 400) String description,
    @NotNull Category category,
    @NotBlank @Size(max = 80) String area,
    @NotBlank @Size(max = 200) String contact
    )
{}
