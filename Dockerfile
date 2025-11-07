FROM amazoncorretto:25-alpine3.22-jdk
RUN apk add --no-cache curl
WORKDIR /app
COPY /build/libs/user-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]