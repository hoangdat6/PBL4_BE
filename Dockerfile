FROM openjdk:17-jdk-slim-buster

WORKDIR /app

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} ./app.jar

COPY src/main/resources .

#ENV JAVA_TOOL_OPTIONS "-Duser.timezone=Asia/Ho_Chi_Minh --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.time=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED"

ENTRYPOINT ["sh", "-c", "java --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.time=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.time.zone=ALL-UNNAMED -Djava.net.preferIPv4Stack=true -Duser.timezone=Asia/Ho_Chi_Minh -jar app.jar"]

EXPOSE 5999
