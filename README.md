# Ride App Backend

## Overview
A backend service for a Ride app, built with Spring Boot and Kotlin. It provides APIs for managing consumers, couriers, delivery tasks, and authentication.

## Features
- Account management: Consumer, Courier, and Merchant registration, retrieve its data and authentication.
- Task management: Create, assign, and update delivery tasks.
- Role-based access control for different user types.

## Tech Stack
- Spring Boot
- Kotlin
- PostgreSQL
- JPA/Hibernate
- Flyway for database migrations
- Docker for containerization


## Quick Start
1. Get Postgres image
```bash
docker pull postgres
```
2. Run local Postgres for testing
```bash
docker run -p 5432:5432 -d \
    --name postgres \
    -e POSTGRES_PASSWORD=postgres \
    -e POSTGRES_USER=postgres \
    -e POSTGRES_DB=postgres \
    postgres
```
3. Run code
```
./gradrew bootRun
```
or
```
docker run --network=host -e "SPRING_PROFILES_ACTIVE=prod" -e "DB_USERNAME=postgres" -e "DB_PASSWORD=postgres" -e "DB_PORT=5432" -e "DB_HOST=localhost" -e "DB_NAME=postgres" 
```

Tips💡
You can use command `make help` to learn simplified CLI ops.

## Link to more detailed documentation:
- [Architecture](doc/architecture.md)
- [Challenges and Learnings](doc/challenges-and-learnings.md)
- [Features](doc/features.md)
- [Testing](doc/testing.md)
