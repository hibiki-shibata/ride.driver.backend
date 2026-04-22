# Architecture

## Frontend
- Tech Stack
    - React
    - TypeScript
    - Tailwind CSS
    - Vite
    - GitHub Pages for hosting
I used reusable components to separate concerns such as task forms, task lists, and filters.

## Backend
- Tech Stack
    - Spring Boot
    - Kotlin
    - PostgreSQL
    - JPA/Hibernate
    - Docker for containerization
    - Google Cloud Run for hosting
It provides RESTful APIs for managing users, tasks, and authentication. The backend handles business logic and data persistence.

## Database
- Tech Stack
    - PostgreSQL
    - JPA/Hibernate for ORM
    - Google Cloud SQL for managed database hosting
PostgreSQL stores consumers, couriers, merchants, tasks, and menu items. I used JPA/Hibernate for ORM and Flyway for database migrations.

## Why This Structure
This architecture allows for a clear separation of concerns, making the application easier to maintain and scale. The frontend focuses on user experience, while the backend handles data management and business logic. Using a relational database like PostgreSQL ensures data integrity and supports complex queries for task management.