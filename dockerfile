FROM openjdk:11-jre-slim
RUN mkdir /app
COPY build/libs/*.jar /app/instore-backend.jar
ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseContainerSupport", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/instore-backend.jar"]