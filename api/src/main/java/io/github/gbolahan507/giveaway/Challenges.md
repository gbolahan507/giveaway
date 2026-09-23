# Challenges

One entry per problem that cost real time. The point is the reasoning,
not just the fix — including the wrong turns.

---

## 2026-09-23 — /actuator/health returned 404 for hours

**Symptom**
`curl localhost:8080/actuator/health` returned 404 with
`NoResourceFoundException: No static resource actuator/health`.
The app appeared to be running — Tomcat answered, Spring MVC routed.

**What I thought it was, in order**

1. Actuator not on the classpath. Wrong — `grep` found it in `pom.xml`.
2. Endpoint not exposed in config. Wrong — checked the Spring Boot 4.1.1
   docs: `health` is exposed at `/actuator/health` by default, no config
   needed.
3. An agent told me every artifact ID in the pom was invented and the
   version 4.1.1 didn't exist, and offered to downgrade to 3.4.1.
   Also wrong, and would have broken a working project. Two things
   disproved it: Maven fails hard on unresolvable dependencies rather
   than skipping them silently, and the app had already served HTTP —
   which it couldn't do if the web starter were fictional.

**What it actually was**

A stale JVM from an earlier run still holding port 8080. Every restart
failed with `Web server failed to start. Port 8080 was already in use`
— and every curl hit the *original* process, which predated the
config. I was testing a version of the app from hours earlier.

**How I found it**

`./mvnw dependency:tree` settled the dependency question in four
seconds — everything resolved, all `4.1.1`, all compile scope. That
ruled out the classpath entirely and forced the question to "did it
start?" Restarting with output to a file surfaced the port error,
which had been in the logs all along, above the part I was reading.

**Next time, check first**

- Did the app actually start? Look for `Started …Application` in the
  log before trusting any HTTP response.
- `lsof -i :8080` — is the thing answering the thing I just built?
- `./mvnw dependency:tree` before arguing about what's on the classpath.

**Wider lesson**

The 404 described the *symptom* accurately and pointed nowhere near the
cause. And a confident, well-formatted explanation from a tool was
wrong in a way that would have cost a day — it invented a Maven
behaviour to explain away a contradiction. When an explanation needs
the tools to be misbehaving, check the tools.