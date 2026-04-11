# GEMINI.md

## Project Overview

This is a Java Spring Boot application that provides a URL shortening service. It allows users to create, manage, and
delete short URLs. The application uses Java 21, Spring Boot, Spring Data JPA with Hibernate, and PostgreSQL for the
database. Authentication is handled using JWT.

The project follows a standard layered architecture with controllers, services, and repositories. It also includes unit
tests for the service layer.

## Building and Running

The project is built using Gradle.

### Building

To build the project, run the following command:

```bash
./gradlew build
```

### Running

To run the application, you can use the following Gradle command:

```bash
./gradlew bootRun
```

The application will start on the port configured in `src/main/resources/application.yaml` (default is 8080).

### Testing

To run the tests, use the following command:

```bash
./gradlew test
```

## Development Conventions

* **Code Style**: The project uses Spotless with the Google Java Format to enforce a consistent code style.
* **Testing**: Unit tests are written using JUnit 5 and Mockito.
* **API**: The application exposes a REST API for managing short URLs. The API endpoints are documented in the
  controller classes.

## Coding Conventions

* **Naming**: Follow standard Java naming conventions for classes, methods, and variables.
* **Error Handling**: Use appropriate exception handling and return meaningful error messages in the API responses.
* **Logging**: Use SLF4J for logging and ensure that sensitive information is not logged.
* **Security**: Ensure that all endpoints are secured using JWT authentication and that user data is protected.
* **DRY**: Avoid code duplication by using service methods and utility classes where appropriate.
* **Optional Usage**: Don't use `Optional` as a return type in service methods.
* **Documentation**: Don't use Javadoc comments for public methods in controllers and services, as the API is
  self-explanatory. Instead, focus on writing clear and concise code.

## Redirection Logic

The redirection logic is handled by the `RedirectController`. This controller has a single endpoint `/{shortCode}` that
takes a short code as a path variable. It then uses the `ShortUrlService` to find the original URL and redirects the
user to that URL.
