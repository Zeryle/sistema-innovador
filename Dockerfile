# ============================================================================
# Stage 1 — Build: produce the Spring Boot fat jar (includes the Angular SPA)
# ============================================================================
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

# Copy the wrapper + pom first so dependency resolution is cached
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Resolve dependencies (cached layer unless pom changes)
RUN ./mvnw -B -q dependency:go-offline || true

# Copy sources
COPY src/ src/
COPY frontend/ frontend/

# Build the fat jar — spring-boot-maven-plugin will run the
# copy-angular-bundle execution to embed frontend/dist/browser/ into
# src/main/resources/static before repackaging.
RUN ./mvnw -B -DskipTests package \
 && cp target/sistema-innovador-*.jar /workspace/app.jar

# ============================================================================
# Stage 2 — Runtime: minimal JRE 17 image
# ============================================================================
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

# Run as non-root for least-privilege
RUN groupadd -r autotaller && useradd -r -g autotaller autotaller

COPY --from=build --chown=autotaller:autotaller /workspace/app.jar /app/app.jar

USER autotaller

EXPOSE 8586

# Sensible container defaults — override via -e or env file
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0" \
    SPRING_PROFILES_ACTIVE=mysql \
    SERVER_PORT=8586

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]

# Image labels
LABEL org.opencontainers.image.title="AutoTaller Backend" \
      org.opencontainers.image.description="Spring Boot 3.5 API with 13 DDD bounded contexts, JWT auth, MySQL, mock Stripe & WhatsApp, Apache POI Excel export, OpenAPI/Swagger UI" \
      org.opencontainers.image.source="https://github.com/Zeryle/sistema-innovador" \
      org.opencontainers.image.licenses="Academic — UTP DWI"