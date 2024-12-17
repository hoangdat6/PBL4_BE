# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17-slim AS build

WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final image
FROM openjdk:17-jdk-slim-buster

WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar ./app.jar

# Set the entry point
ENTRYPOINT ["sh", "-c", "java --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.time=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.time.zone=ALL-UNNAMED -Djava.net.preferIPv4Stack=true -Duser.timezone=Asia/Ho_Chi_Minh -Dspring.datasource.url=${SPRING_DATASOURCE_URL:-jdbc:mysql://db:3306/pbl4_be?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true} -Dspring.datasource.username=${SPRING_DATASOURCE_USERNAME:-root} -Dspring.datasource.password=${SPRING_DATASOURCE_PASSWORD:-dat123} -jar app.jar"]

EXPOSE 5999