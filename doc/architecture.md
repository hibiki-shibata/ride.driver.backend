# Architecture

## Frontend
The frontend was built with React. I used reusable components to separate concerns such as task forms, task lists, and filters.

## Backend
The backend was built with Spring Boot and Kotlin. It provides RESTful APIs for managing users, tasks, and authentication. The backend handles business logic and data persistence.

## Database
PostgreSQL stores consumers, couriers, merchants, tasks, and menu items. I used JPA/Hibernate for ORM and Flyway for database migrations.

## Why This Structure
This architecture allows for a clear separation of concerns, making the application easier to maintain and scale. The frontend focuses on user experience, while the backend handles data management and business logic. Using a relational database like PostgreSQL ensures data integrity and supports complex queries for task management.