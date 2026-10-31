### GET /items
Auth: optional

Query
category   optional   FURNITURE | KITCHEN | BOOKS | CLOTHING | OTHER
area       optional   free text, matched case-insensitively
page       optional   default 0
size       optional   default 20, max 50


**200 OK**
{
"items": [
{
"id": "9f3c...",
"title": "Desk lamp",
"thumbnailUrl": "https://.../thumb.jpg",
"category": "FURNITURE",
"area": "Stevenage, SG1",
"status": "AVAILABLE",
"postedAt": "2026-09-23T12:10:00Z"
}
],
"page": 0, "size": 20, "totalPages": 4, "totalItems": 73
}

**400** — size above 50, or page below 0

All items, newest first — claimed ones included, marked by status.
The client greys those rows and disables the tap.

Enums (category, status) travel as SCREAMING_CASE.
The UI decides how to display them; the API never sends display text.


### GET /items/{id}

Auth: optional — a token changes what comes back

Success 200
{
"id": "8f3a2b1c-...",
"title": "Desk lamp",
"description": "Adjustable arm, warm bulb included. Works
perfectly — bought a second by mistake.",
"category": "FURNITURE",
"area": "Stevenage",
"photoUrls": [],
"postedBy": {
"id": "4c7e9a2f-...",
"displayName": "Amara O."
},
"postedAt": "2026-09-23T08:12:00Z",
"status": "AVAILABLE",
"claimedByMe": false,
"contact": null
}

Failures
400  id is not a valid UUID
→ treat as not found in the UI
404  no item with that id
→ "This item is no longer available."



### POST /items
Auth: required

Request
{
"title": "Desk lamp",
"description": "Adjustable arm, warm bulb included.",
"category": "FURNITURE",
"area": "Stevenage",
"contact": "07700 900123"
}

Success 201
{
"id": "8f3a2b1c-...",
"title": "Desk lamp",
"description": "Adjustable arm, warm bulb included.",
"category": "FURNITURE",
"area": "Stevenage",
"photoUrls": [],
"postedBy": { "id": "4c7e9a2f-...", "displayName": "Hameed" },
"postedAt": "2026-09-23T14:02:00Z",
"status": "AVAILABLE",
"claimedByMe": false,
"contact": "07700 900123"
}

Failures
401  no token, or expired
→ bounce to login
400  title blank or > 100 chars
title too short (< 3)
description > 2000 chars
category not one of the five
area blank
contact blank
→ field-level messages under each input



## GET /me/items

Items I posted. Auth required. Powers the Posted tab.

**200 OK**
{
"items": [
{
"id": "9f3c...",
"title": "Rice cooker",
"thumbnailUrl": "https://.../thumb.jpg",
"category": "KITCHEN",
"status": "CLAIMED",
"claimedByName": "Tomas B.",
"claimedAt": "2026-09-21T18:04:00Z",
"postedAt": "2026-09-19T09:12:00Z"
}
],
"page": 0, "size": 20, "totalPages": 1, "totalItems": 4
}

**401** — no token, or expired

Includes claimed items — unlike GET /items, which hides them.
claimedByName and claimedAt are null while status is AVAILABLE.


## GET /me/claims

Items I claimed. Auth required. Powers the Claimed tab.

**200 OK**
{
"items": [
{
"id": "9f3c...",
"title": "Rice cooker",
"thumbnailUrl": "https://.../thumb.jpg",
"category": "KITCHEN",
"area": "Stevenage, SG1",
"postedByName": "Amara O.",
"contact": "07700 900123",
"claimedAt": "2026-09-21T18:04:00Z"
}
],
"page": 0, "size": 20, "totalPages": 1, "totalItems": 2
}

**401** — no token, or expired


## DELETE /items/{id}/claim

Release an item I claimed. Auth required.

**Request:** no body.

**204 No Content** — claim removed, item is Available again.

**400** — id is not a valid UUID
**401** — no token, or expired
**404** — no item with that id, or you have no claim on it