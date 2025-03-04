# SocialMediaPlatform

## Overview
SocialMediaPlatform is a Spring Boot-based application that provides social media functionalities.

## Prerequisites
Before running the application, ensure you have the following installed:

- **Java 21**
- **Gradle 8.12.1**
- **MySQL** (or any other configured database)
- **Git** (optional, for cloning the repository)

## Installation & Setup

### 1. Clone the Repository
```sh
git clone https://github.com/your-username/SocialMediaPlatform.git
cd SocialMediaPlatform
```

### 2. Configure the Database
Update  `application.properties` with your database settings:

```properties
spring.datasource.url= jdbc:mysql://localhost:3306/user_posts
spring.datasource.username= <db_username>
spring.datasource.password= <db_password>
spring.datasource.driver-class-name= com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto= none
spring.jpa.show-sql=true
```

Run the `sql script`.
```mysql
create database user_posts;

use user_posts;

CREATE TABLE users(
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      username VARCHAR(100) UNIQUE NOT NULL,
                      email VARCHAR(100) UNIQUE NOT NULL,
                      password VARCHAR(100) NOT NULL,
                      profile_picture VARCHAR(100),
                      bio TEXT,
                      role VARCHAR(100) NOT NULL
);

-- A Post has one user.

CREATE TABLE posts(
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      user_id BIGINT NOT NULL,
                      content TEXT NOT NULL,
                      timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- A post has multiple comments.

CREATE TABLE comments(
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         post_id BIGINT NOT NULL,
                         content TEXT NOT NULL,
                         timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                         FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

-- A user can follow multiple users (except themselves).
-- User - Follow {Many to Many Relationship}
CREATE TABLE follows(
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        follower_id BIGINT NOT NULL,
                        following_id BIGINT NOT NULL,
                        FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
                        FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE,
                        UNIQUE (follower_id, following_id), -- Prevent duplicate follows,
                        CHECK (follower_id <> following_id) -- Prevent self-following
);

-- A user can like a post.
-- A post can have multiple likes.
CREATE TABLE post_likes(
                           post_id BIGINT NOT NULL,
                           user_id BIGINT NOT NULL,
                           FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                           PRIMARY KEY (post_id,user_id)
);
```

### 3. Build the Project
```sh
./gradlew clean build
```

### 4. Run the Application
```sh
./gradlew bootRun
```

Alternatively, you can run the generated JAR file:
```sh
java -jar target/social-media-platform-0.0.1-SNAPSHOT.jar
```

### 5. Test the APIs using the `UserRequests.http` & `PostRequests.http` files.

## API Documentation
Swagger UI is enabled for API documentation. Once the application is running, you can access it at:

- **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **API Docs:** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

### Links

- **JPA/Hibernate** [https://www.baeldung.com/jpa-hibernate-associations](https://www.baeldung.com/jpa-hibernate-associations)
- **JWT Authentication (1)** [https://www.youtube.com/watch?v=HYBRBkYtpeo&t=2219s&ab_channel=GenuineCoder](https://www.youtube.com/watch?v=HYBRBkYtpeo&t=2219s&ab_channel=GenuineCoder)
- **JWT Authentication (2)** [https://www.javaguides.net/2024/01/spring-boot-security-jwt-tutorial.html](https://www.javaguides.net/2024/01/spring-boot-security-jwt-tutorial.html)
- **RestAPI Integration Testing** [https://www.javaguides.net/2022/03/spring-boot-integration-testing-mysql-crud-rest-api-tutorial.html](https://www.javaguides.net/2022/03/spring-boot-integration-testing-mysql-crud-rest-api-tutorial.html)