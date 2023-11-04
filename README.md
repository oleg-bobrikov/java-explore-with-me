# Explore With Me

---

## Stack: 
- Java 11
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Docker
- Maven
---

Free time is a valuable resource. Every day, we plan how to spend it – where to go and with whom.  
The most challenging part of this planning is finding information and negotiating.  
You need to consider many details: what events are planned, whether friends are available at that moment,  
how to invite everyone, and where to meet.
"Explore With Me" is an event listing. In this event listing, you can propose any event,  
from an exhibition to a trip to the movies, and gather a group to participate in it.
---

## Microservice architecture
The application consists of 2 services:

- stats-service - a part of the application that collects, stores, and provides statistics on views upon request.
- main-service - the core part of the application where all the application's logic takes place.

---

### The event lifecycle consists of several stages:
- PENDING
- CANCELED
- PUBLISHED 

### There are 3 actors:
- Initiator (owner of event)
- Administrator (moderator)
- Participant (anyone who want to take a part in event)
- 
Initiator can create and event.
The event get PENDING state immediately after Initiator save it in a database.  
The Administrator can make a decision to approve or reject publication.

Everyone can take part an event and make a request for participation.
Initiator can make a decision to approve or reject participation.
It's possible to take part in a event only in a PUBLISHED event.

Each GET event request saved in statistics service.
---
## main-service database schema (https://dbdiagram.io/d/explore-with-me-654191b67d8bbd64653c2052)
![Diagram](main-serivce-schema-db.png)

## stat-service database schema (https://dbdiagram.io/d/explore-with-me-stats-65469ec17d8bbd64657d8f1c)
![Diagram](stats-db.png)
---

### PR https://github.com/oleg-bobrikov/java-explore-with-me/pull/5

