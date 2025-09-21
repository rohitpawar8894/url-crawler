# Build stage (Maven 3.8.5 + Java 11)
FROM maven:3.8.5-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
COPY crawler-rest-api/pom.xml ./crawler-rest-api/
RUN mvn -q -DskipTests dependency:go-offline
COPY . .
WORKDIR /app/crawler-rest-api
RUN mvn -q -DskipTests package

# Run stage (Java 11 JRE)
FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=build /app/crawler-rest-api/target/crawler-rest-api-1.0.0.jar app.jar
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]