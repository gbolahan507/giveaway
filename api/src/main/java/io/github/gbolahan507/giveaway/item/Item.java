package io.github.gbolahan507.giveaway.item;

import io.github.gbolahan507.giveaway.auth.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "items", indexes = {

        @Index(name = "idx_itemsposted_by", columnList = "posted_by"),

        @Index(name = "idx_items_claimed_by", columnList = "claimed_by")

})
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;



    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 4000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false, length = 80)
    private String area;

    @Column(nullable = false, length = 200)
    private String contact;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatus status = ItemStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "posted_by", nullable = false)
    private User postedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claimed_by")
    private User claimedBy;

    @Column(nullable = false, updatable = false)
    private Instant postedAt;

    private Instant claimedAt;

    protected Item() { }

    public Item(String title, String description, Category category,
                String area, String contact, User postedBy) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.area = area;
        this.contact = contact;
        this.postedBy = postedBy;
        this.status = ItemStatus.AVAILABLE;
        this.postedAt = Instant.now();
    }

    public UUID getId()            { return id; }
    public String getTitle()       { return title; }
    public String getDescription() { return description; }
    public Category getCategory()  { return category; }
    public String getArea()        { return area; }
    public String getContact()     { return contact; }
    public ItemStatus getStatus()  { return status; }
    public User getPostedBy()      { return postedBy; }
    public User getClaimedBy()     { return claimedBy; }
    public Instant getPostedAt()   { return postedAt; }
    public Instant getClaimedAt()  { return claimedAt; }
}