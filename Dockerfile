# Use stable OpenJDK 17 image
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy Maven wrapper and config
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Give execute permission to mvnw (IMPORTANT FIX)
RUN chmod +x mvnw

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests -B

# Expose application port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["sh", "-c", "java -Dserver.port=$PORT -jar target/onlinebank-0.0.1-SNAPSHOT.jar"]