# 25 is the current LTS. 26 is non-LTS, security patches end ~Sept 2026.
FROM docker.io/eclipse-temurin:25-jdk AS build

WORKDIR /app

RUN apt-get update && apt-get install -y unzip
RUN mkdir -p /app
COPY gradle /app/gradle
COPY gradle.properties gradlew /app/
# So we keep gradle  downloaded
RUN ./gradlew
COPY build.gradle.kts settings.gradle.kts /app/
# NOTE: only `assets` belongs here. Listing `src`/`gradle` as extra sources made
# Docker merge them into /app/assets/, and Routing.kt serves that directory
# statically -> the Kotlin sources were published at /assets/main/kotlin/...
COPY assets /app/assets/
COPY src /app/src/
RUN ./gradlew installDist

FROM docker.io/eclipse-temurin:25-jdk
WORKDIR /app
# curl is what the container HEALTHCHECK and the compose healthcheck probe /health with.
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
RUN mkdir -p /app
COPY --from=build /app/build/install/nap-web /app
ENV ENVIRONMENT=production
ENV SPARQL_SERVER=http://nap-sparql:3030/raw/sparql
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=45s --retries=3 \
  CMD curl -fsS http://localhost:8080/health || exit 1
ENTRYPOINT ["./bin/nap-web"]
