# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17-slim AS build

WORKDIR /app

# Copy source code and build application
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests && \
    rm -rf ~/.m2/repository ~/.m2/settings.xml /app/target/generated-sources

# Stage 2: Create the final image
FROM openjdk:17-jdk-alpine

WORKDIR /app

# Copy JAR file from build stage
COPY --from=build /app/target/*.jar ./app.jar

# Set the entry point
ENTRYPOINT ["sh",\
 "-c", \
 "java --add-opens java.base/java.util=ALL-UNNAMED \
 --add-opens java.base/java.time=ALL-UNNAMED \
 --add-opens java.base/java.lang=ALL-UNNAMED \
 --add-opens java.base/java.time.zone=ALL-UNNAMED \
 -Djava.net.preferIPv4Stack=true \
 -Duser.timezone=Asia/Ho_Chi_Minh \
 -Dspring.datasource.url=${SPRING_DATASOURCE_URL} \
 -Dspring.datasource.username=${SPRING_DATASOURCE_USERNAME} \
 -Dspring.datasource.password=${SPRING_DATASOURCE_PASSWORD} \
 -jar app.jar"]

