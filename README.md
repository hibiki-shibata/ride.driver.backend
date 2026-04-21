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
`

Tips💡
You can use command `make help` to learn simplified CLI ops.

### Manual Initiations For Mac/Linux
1. `./gradlew bootRun` - Run with Spring plugin (Recommended)
2. `./gradlew run` - Compile and Run Script (Enable "application" plugin)
3. `./gradle build` - Compile jar file with required classpath & MainClass spec
4. `java -jar ./app/build/libs/*OT.jar` - Run compiled jar file
- Spring Plug in(@SpringBootApplication) helps automatically find the mainClass to include it in manifest
- <file_name>.plane.jar is complied file without mainClass
- <file_name>.jar is complied file with mainClass


## Flyway control
--memo--
- Flyway is responsible only for DB migration - Create tables or stuffs.
- Spring JPA is reading @Entity or @Table stuffs, to validate and use ORM
- Data type of columns in SQL have to be corresponding to the data type of model Entities - JPA valdate it.
