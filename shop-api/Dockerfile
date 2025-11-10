FROM amazoncorretto:21-alpine-jdk
COPY shop-api/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 8080