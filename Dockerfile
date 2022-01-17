FROM maven:3.6.0-jdk-11-slim AS builder
COPY . /usr/src/fxrates
WORKDIR /usr/src/fxrates
RUN mvn clean package

FROM openjdk:8-jdk-alpine
WORKDIR /usr/src/fxrates
COPY --from=builder /usr/src/fxrates/target/fxrates-1.0-SNAPSHOT.jar .
COPY --from=builder /usr/src/fxrates/src/main/resources/*.properties .
EXPOSE 8080
ENV PROFILE=default
ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILE}", "-jar", "fxrates-1.0-SNAPSHOT.jar", "com.myalc.fxrates.Application"]
