# job4j_github_analysis

![Build Status](https://github.com/JenBrainnet/job4j_github_analysis/actions/workflows/maven.yml/badge.svg)

## Project Description

GitHub Analysis is a training Spring Boot REST API for collecting repository and commit data from GitHub.
The project is developed as part of the Job4j course.

The application can save GitHub repositories to a PostgreSQL database and periodically load commits for the saved repositories.
Repository loading can be started manually through the REST API, while commit loading is automated with Spring Scheduler.

Example:

```text
GitHub profile:
JenBrainnet

Repository:
https://github.com/JenBrainnet/job4j_github_analysis

Stored data:
repository name, repository URL, commit message, author, commit date
```

## How It Works

Flow:

1. A client sends `POST /api/repositories/{username}`.
2. The application requests `https://api.github.com/users/{username}/repos`.
3. Received repositories are saved to the `repositories` table.
4. Scheduler periodically reads saved repositories from the database.
5. For each repository, the application requests GitHub commits.
6. New commits are saved to the `commits` table.
7. A client can request repositories or commits through REST endpoints.

## API Overview

Endpoints:

- `GET /api/repositories` returns all saved repositories.
- `GET /api/commits/{name}` returns commits for the repository with the specified name.
- `POST /api/repository` saves one repository from the request body.
- `POST /api/repositories/{username}` loads repositories for a GitHub user.

Example request:

```bash
curl -X POST http://localhost:8080/api/repositories/JenBrainnet
```

Example response:

```text
204 No Content
```

## Scheduled Commit Loading

Commit loading is configured with Spring Scheduler.
The interval is set in `src/main/resources/application.properties`:

```properties
scheduler.fixedRate=3600000
spring.task.scheduling.pool.size=10
```

By default, commits are loaded once per hour.

## Technology Stack

- Java 17
- Spring Boot 3.3.0
- Spring Web
- Spring Data JPA
- Spring Scheduler
- Spring Async
- RestTemplate
- PostgreSQL
- Liquibase
- Maven
- JUnit 5
- Mockito

## Environment Requirements

- Java 17+
- Maven 3.8+
- PostgreSQL 14+

## Project Launch

1. Create the PostgreSQL database:

```sql
CREATE DATABASE job4j_github_analysis;
```

2. Check database settings in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://127.0.0.1:5432/job4j_github_analysis
spring.datasource.username=postgres
spring.datasource.password=password
```

3. Run the application:

```bash
mvn spring-boot:run
```

4. Load repositories for a GitHub user:

```bash
curl -X POST http://localhost:8080/api/repositories/JenBrainnet
```

5. Check saved repositories:

```bash
curl http://localhost:8080/api/repositories
```

6. Run tests:

```bash
mvn test
```

## Contacts

- GitHub: [JenBrainnet](https://github.com/JenBrainnet)
