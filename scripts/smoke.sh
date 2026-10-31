#!/usr/bin/env bash
#
# Smoke test for the giveaway API.
# Creates an item, then exercises all four claim outcomes.
#
# Prerequisites: docker compose up -d, app running on 8080,
# and the three seeded users below existing in the database.
#
# Usage: ./scripts/smoke.sh

set -euo pipefail

BASE=http://localhost:8080

HAMEED=90a744eb-6f6b-4d18-9f0a-2ae499b1e3b3
BOB=acda5ec8-9403-4903-bb47-14dc765a9a81
CAROL=7a161211-d7f6-447b-8c8d-4a127bfd49a9

# expect <label> <expected-status> <user-id>
# Claims $ITEM as the given user and checks the status code.
expect() {
  local label=$1
  local want=$2
  local user=$3
  local got

  got=$(curl -s -o /dev/null -w '%{http_code}' \
        -X POST "$BASE/items/$ITEM/claim" \
        -H "Giveaway-User-Id: $user")

  if [ "$got" = "$want" ]; then
    echo "ok    $label → $got"
  else
    echo "FAIL  $label → expected $want, got $got"
    exit 1
  fi
}

echo "--- creating item as Hameed"

ITEM=$(curl -s -X POST "$BASE/items" \
  -H "Content-Type: application/json" \
  -H "Giveaway-User-Id: $HAMEED" \
  -d '{
        "title": "Blue two-seater sofa",
        "description": "Good condition, collection only",
        "category": "FURNITURE",
        "area": "Luton",
        "contact": "07700 900000"
      }' \
  | python3 -c 'import sys, json; print(json.load(sys.stdin)["id"])')

echo "      item $ITEM"

echo "--- contact hidden from a stranger"

contact=$(curl -s "$BASE/items/$ITEM" \
  -H "Giveaway-User-Id: $BOB" \
  | python3 -c 'import sys, json; print(json.load(sys.stdin)["contact"])')

if [ "$contact" = "None" ]; then
  echo "ok    bob sees no contact"
else
  echo "FAIL  bob saw contact: $contact"
  exit 1
fi

echo "--- claim outcomes"

expect "bob claims"        200 "$BOB"
expect "bob claims again"  200 "$BOB"
expect "hameed own item"   403 "$HAMEED"
expect "carol too late"    409 "$CAROL"

echo
echo "all passed"