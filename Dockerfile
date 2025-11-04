# Stage 1: Build the app using Maven
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY luddy.db /app/
RUN mvn clean package -DskipTests

# Stage 2: Run the built JAR
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/target/room-reservation-0.0.1-SNAPSHOT.jar app.jar
COPY luddy.db ./
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
