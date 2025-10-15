# ----------------------------
# Stage 1: Build the application
# ----------------------------
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy Maven project descriptor and resolve dependencies to cache them separately
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the application source code and build the project, skipping tests
COPY src ./src
RUN mvn clean package -DskipTests

# ----------------------------
# Stage 2: Create runtime image
# ----------------------------
FROM eclipse-temurin:21-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Define the default command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
