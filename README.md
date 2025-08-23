# Spring Boot Library REST API

A comprehensive RESTful API for a bookstore management system, built with Java and Spring Boot. This project serves as a practical demonstration of modern back-end development principles, including robust security, database management, and cloud deployment.

## ✨ Key Features

-   **Full CRUD Operations**: Endpoints for managing books and authors (Create, Read, Update, Delete).
-   **Robust Security**: Implemented a secure authentication and authorization system using **Spring Security**, with support for both **JWT (JSON Web Tokens)** for stateless authentication and **OAuth2** with Google as an identity provider.
-   **Role-Based Access Control**: Differentiated access levels for `USER` and `ADMIN` roles, protecting sensitive endpoints.
-   **Database Management**: Modeled and managed a PostgreSQL database using **Spring Data JPA** and Hibernate for efficient data persistence.
-   **API Documentation**: Integrated **SpringDoc (Swagger UI)** for clear, interactive, and auto-generated API documentation.
-   **Containerized Deployment**: Application is fully containerized using **Docker**, ensuring a consistent and isolated environment for deployment on platforms like **AWS**.

---

## 🛠️ Technology Stack

This project is built with a modern and robust set of technologies:

-   **Framework**: Spring Boot 3
-   **Language**: Java 17
-   **Database**: PostgreSQL
-   **Security**: Spring Security, JWT, OAuth2
-   **ORM**: Spring Data JPA, Hibernate
-   **API Documentation**: SpringDoc (OpenAPI 3)
-   **Containerization**: Docker
-   **Build Tool**: Maven
-   **Utilities**: Lombok, MapStruct

---

## 🚀 Getting Started

Follow these instructions to get a local copy of the project up and running for development and testing purposes.

### Prerequisites

-   [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or later
-   [Apache Maven](https://maven.apache.org/download.cgi)
-   [Docker](https://www.docker.com/products/docker-desktop/) and Docker Compose
-   A PostgreSQL instance (or you can use the provided `docker-compose.yml`)

### Installation & Setup

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/joao-tinelli/springboot-library-api.git](https://github.com/joao-tinelli/springboot-library-api.git)
    cd springboot-library-api
    ```

2.  **Set up the database with Docker (Recommended for Dev):**
    This is the easiest way to get a PostgreSQL database running for local development.
    ```bash
    docker-compose up -d
    ```
    This command will start a PostgreSQL container with the default settings specified in the `application.yml`.

3.  **Configure Environment Variables for Local Run:**
    If you plan to run the app locally with `mvn spring-boot:run`, you must configure the environment variables for database credentials and Google OAuth2. You can set them in your IDE's run configuration or export them in your terminal.

    **For Database Connection (if not using Docker Compose defaults):**
    ```bash
    export DATASOURCE_URL=jdbc:postgresql://localhost:5432/library
    export DATASOURCE_USERNAME=postgres
    export DATASOURCE_PASSWORD=postgres
    ```

    **For Google OAuth2 Login:**
    ```bash
    export GOOGLE_CLIENT_ID=<your-google-client-id>
    export GOOGLE_CLIENT_SECRET=<your-google-client-secret>
    ```

4.  **Run the application locally:**
    ```bash
    mvn spring-boot:run
    ```
    The API will be available at `http://localhost:8080`.

---

## 🐳 Running with Docker (Production-like)

The provided `Dockerfile` allows you to build a self-contained image of the application. This is ideal for deploying the application in a production or staging environment.

1.  **Build the Docker image:**
    From the root of the project, run the following command to build the image.
    ```bash
    docker build -t library-api .
    ```

2.  **Run the Docker container:**
    When running the container, you must provide the necessary environment variables for the database connection and Google OAuth2 credentials. Make sure the application container can connect to your PostgreSQL instance (it could be another Docker container or a cloud database).

    ```bash
    docker run -p 8080:8080 \
      -e SPRING_PROFILES_ACTIVE=production \
      -e DATASOURCE_URL=<your-db-url> \
      -e DATASource_USERNAME=<your-db-username> \
      -e DATASOURCE_PASSWORD=<your-db-password> \
      -e GOOGLE_CLIENT_ID=<your-google-client-id> \
      -e GOOGLE_CLIENT_SECRET=<your-google-client-secret> \
      library-api
    ```
    The application will now be running inside a Docker container and accessible at `http://localhost:8080`.

---

## 📖 API Documentation

The API is documented using SpringDoc and OpenAPI 3. Once the application is running, you can access the interactive Swagger UI to explore and test the endpoints.

-   **Swagger UI URL**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

You will find a complete list of available endpoints, request/response models, and the ability to execute API calls directly from your browser.
