FROM openjdk:21

WORKDIR /app

ADD target/schedule-service-0.0.1-SNAPSHOT.jar .

CMD ["java", "-jar", "schedule-service-0.0.1-SNAPSHOT.jar"]