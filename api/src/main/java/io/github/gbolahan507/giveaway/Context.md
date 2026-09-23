# Giveaway

An app for passing on things you no longer need. Someone posts surplus
— furniture, kitchen items, books — and a neighbour claims it. Nothing
is sold. Everything is free.

Built as two halves by one person: a Flutter app (`app/`) and a Spring
Boot API (`api/`).

## Stack

- Java 21, Spring Boot 4.1.1, Maven
- PostgreSQL 16 in Docker (host port 5433)
- Flutter client

## Scope — v1

Six endpoints, deliberately. Nothing else ships until these work.

1. `POST /auth/register`
2. `POST /auth/login`
3. `GET /items` — paginated, filter by category and area
4. `GET /items/{id}`
5. `POST /items`
6. `POST /items/{id}/claim` and `DELETE /items/{id}/claim`

Five screens: Auth, Browse, Item detail, Post an item, My stuff.

## Explicitly out of scope

Messaging, ratings, maps or geolocation, push notifications, identity
verification, image upload (v2), payments (never — the app is for
giving).

## Decisions

**API is shaped to screens, not to tables.** This is a Backend-for-
Frontend. The Browse response carries six fields because that's what
the row renders; the detail response carries more. Entities are never
returned directly.

**Timestamps go to the client as ISO-8601.** The server does not
format "2 hours ago" — it doesn't know the reader's timezone or when
they'll look.

**Claiming is the hard part.** One item, two claimants, exactly one
wins. Optimistic locking, not "check then update".

**Packages by feature** (`auth`, `item`, `claim`), not by layer.
Repositories stay package-private so the only way into a feature is
through its service.

## Open questions

- Contact detail: on the Item or on the User?
- Does releasing a claim put the item straight back into circulation?

## Running it

```
docker compose up -d
cd api && ./mvnw spring-boot:run
curl localhost:8080/actuator/health
```