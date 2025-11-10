FROM amazoncorretto:21-alpine-jdk
COPY payments-api/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 8081