FROM openjdk:8-jdk-alpine as builder
COPY . /usr/src/fxrates
WORKDIR /usr/src/fxrates

RUN apt-get install maven
RUN mvc clean package

# stage 2
FROM openjdk:8-jdk-alpine
WORKDIR /usr/src/fxrates
COPY --from=builder /usr/src/fxrates/target .
ENTRYPOINT ["java", "-jar", "target/fxrates-1.0-SNAPSHOT.jar", "com.myalc.fxrates.Application"]

