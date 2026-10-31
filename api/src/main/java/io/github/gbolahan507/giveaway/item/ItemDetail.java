package io.github.gbolahan507.giveaway.item;

import java.time.Instant;
import java.util.UUID;

public record ItemDetail(
        UUID id,
        String title,
        String description,
        Category category,
        String area,
        ItemStatus status,
        String postedByName,
        Instant postedAt,
        Instant claimedAt,
        boolean claimedByMe,
        String contact
) {
    public static ItemDetail from(Item item, UUID viewerId) {
       boolean isPoster = item.getPostedBy().getId().equals(viewerId);

       boolean isClaimer = item.getClaimedBy() != null && item.getClaimedBy().getId().equals(viewerId);

       return  new ItemDetail(
               item.getId(),
               item.getTitle(),
               item.getDescription(),
               item.getCategory(),
               item.getArea(),
               item.getStatus(),
               item.getPostedBy().getDisplayName(),
               item.getPostedAt(),
               item.getClaimedAt(),
               isClaimer,
               (isPoster || isClaimer) ? item.getContact() : null


       );
    }
}
