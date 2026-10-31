package io.github.gbolahan507.giveaway.auth;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String displayName;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected User() { }

    public User(String email, String passwordHash, String displayName) {
        this.email = email.trim().toLowerCase();
        this.passwordHash = passwordHash;
        this.displayName = displayName.trim();
        this.createdAt = Instant.now();
    }

    public UUID getId()           { return id; }
    public String getEmail()      { return email; }
    public String getDisplayName(){ return displayName; }
    public Instant getCreatedAt() { return createdAt; }

    /** Only the auth service should touch this — used to verify a login attempt. */
    public String getPasswordHash() { return passwordHash; }
}