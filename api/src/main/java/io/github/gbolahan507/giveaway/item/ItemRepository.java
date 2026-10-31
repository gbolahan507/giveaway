package io.github.gbolahan507.giveaway.item;

import io.github.gbolahan507.giveaway.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {

    // A repository holds queries and not action

    Page<Item> findByArea(String area, Pageable pageable);
    Page<Item> findByCategory(Category category, Pageable pageable);
    Page<Item> findByPostedBy(User postedBy, Pageable pageable);
    Page<Item> findByClaimedBy(User claimedBy, Pageable pageable);


    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Item i
        SET i.status = ItemStatus.CLAIMED, i.claimedBy = :user, i.claimedAt = :now
        WHERE i.id = :id AND i.status = ItemStatus.AVAILABLE
        """)
    int claim(@Param("id") UUID id, @Param("user") User user, @Param("now") Instant now);


    @Modifying
    @Query("""
        UPDATE Item i
        SET i.status = :newStatus, i.claimedBy = null, i.claimedAt = null
        WHERE i.id = :id AND i.status = :expectedStatus AND i.claimedBy = :user
        """)
    int releaseClaim(UUID id, User user, ItemStatus newStatus, ItemStatus expectedStatus);

    @Query("""
        SELECT i FROM Item i
        LEFT JOIN FETCH i.postedBy
        LEFT JOIN FETCH i.claimedBy
        WHERE i.id = :id
        """)
    Optional<Item> findWithUsersById(@Param("id") UUID id);









}
