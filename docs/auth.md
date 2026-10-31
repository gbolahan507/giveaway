### POST /auth/register
Auth: not required

Request
{
"email": "hameed@example.com",
"password": "at least 8 characters",
"displayName": "Hameed"
}

Success 201
{
"token": "eyJhbGciOi...",
"user": {
"id": "8f3a2b1c-...",
"email": "hameed@example.com",
"displayName": "Hameed"
}
}

Failures
400  missing or invalid fields (bad email format, password < 8,
blank display name)
→ field-level messages under each input
409  email already registered
→ "That email already has an account. Log in instead?"


### POST /auth/login
Auth: not required

Request
{
"email": "hameed@example.com",
"password": "password1"
}

Success 200
{
"token": "eyJhbGciOi...",
"user": {
"id": "8f3a2b1c-...",
"email": "hameed@example.com",
"displayName": "Hameed"
}
}

Failures
400  missing or invalid fields
→ field-level messages under each input
401  no account with that email
→ "We don't have an account with that email. Sign up?"
401  wrong password
→ "That password isn't right."


### Notes on both

Same response shape. Both endpoints end in "you are now
authenticated", so both return one AuthResponse — one record on the
server, one model on the client, one code path for being logged in.

201 for register (a user was created), 200 for login (nothing was).

401, not 403. 401 is "I don't know who you are"; 403 is "I know, and
no". A failed login is the first.

Account existence is discoverable, deliberately. Register returns 409
on a duplicate email, so merging login's two failure cases would hide
nothing while costing real usability — a user with three email
addresses deserves to be told which one has the account. If account
existence ever became sensitive, both endpoints would have to close
together: register would return 200 regardless and send an email
either way.

Never returned, in any form: the password, or its hash.

Missing for production: rate limiting on login. Without it, passwords
can be brute-forced at whatever rate the server answers.