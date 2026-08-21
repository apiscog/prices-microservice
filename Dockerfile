# syntax=docker/dockerfile:1.7

FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml ./

RUN sed -i 's/\r$//' mvnw \
    && chmod +x mvnw

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw --batch-mode --no-transfer-progress dependency:go-offline

COPY src src

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw --batch-mode --no-transfer-progress -DskipTests package

FROM eclipse-temurin:21-jre-jammy AS runtime

RUN apt-get update \
    && apt-get install --yes --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --gid 10001 app \
    && useradd --uid 10001 --gid app --home-dir /app --shell /usr/sbin/nologin --no-create-home app

WORKDIR /app

COPY --from=build --chown=app:app \
    /workspace/target/prices-microservice-0.0.1-SNAPSHOT.jar \
    /app/application.jar

USER 10001:10001

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/application.jar"]
