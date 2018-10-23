FROM maven:3.5-jdk-8-alpine as builder

WORKDIR /build

COPY pom.xml .

RUN mvn -Dmaven.repo.local=/repo clean package

COPY src src

RUN mvn -o -Dmaven.repo.local=/repo -Dassembly.skipAssembly=true package

FROM openjdk:8-jdk

WORKDIR /app

EXPOSE 8080

COPY --from=builder /build/target/application.jar .

ENTRYPOINT ["java", "-jar", "application.jar"]