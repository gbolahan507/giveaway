package io.github.gbolahan507.giveaway.item;

import io.github.gbolahan507.giveaway.auth.User;
import io.github.gbolahan507.giveaway.auth.UserRepository;
import io.github.gbolahan507.giveaway.common.CannotClaimOwnItemException;
import io.github.gbolahan507.giveaway.common.ItemAlreadyClaimedException;
import io.github.gbolahan507.giveaway.common.ItemNotFoundException;
import io.github.gbolahan507.giveaway.common.UserNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;

import java.util.UUID;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    public  ItemService ( ItemRepository itemRepository, UserRepository userRepository, Clock clock ) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.clock = clock;
    }





    @Transactional
    public ItemDetail claim(UUID itemId, UUID userId) {

        Item existing = itemRepository.findWithUsersById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));          // 404

        if (existing.getPostedBy().getId().equals(userId)) {
            throw new CannotClaimOwnItemException(itemId);                      // 403
        }
        if (existing.getClaimedBy() != null
                && existing.getClaimedBy().getId().equals(userId)) {
            return ItemDetail.from(existing, userId);                           // 200, idempotent
        }

        User claimer = userRepository.getReferenceById(userId);

        int updated = itemRepository.claim(itemId, claimer, clock.instant());

        // both findById() and getReferenceById() retrieve an entity by its ID, but they behave differently.
        // findById : This actually queries the database immediately.
        // getReferenceById : This usually gives you a lazy reference/proxy instead of immediately loading the entire entity.


        if (updated == 0) {
            throw new ItemAlreadyClaimedException(itemId);                      // 409
        }

        return ItemDetail.from(
                itemRepository.findWithUsersById(itemId).orElseThrow(), userId);
    }


    @Transactional
    public ItemDetail post(CreateItemRequest request, UUID userId) {
        User poster = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        Item item = new Item(
                request.title(),
                request.description(),
                request.category(),
                request.area(),
                request.contact(),
                poster
        );

        return ItemDetail.from(itemRepository.save(item), userId);
    }


    @Transactional(readOnly = true)
    public ItemDetail detail(UUID itemId, UUID viewerId) {
        Item item = itemRepository.findWithUsersById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        return ItemDetail.from(item, viewerId);
    }



}
