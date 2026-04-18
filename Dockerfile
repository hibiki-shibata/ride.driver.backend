# Build stage
FROM gradle:8.10.2-jdk21 AS build
WORKDIR /builds
COPY . /builds
RUN ./gradlew bootJar --no-daemon

# Run time environment
FROM eclipse-temurin:21
RUN addgroup --system hibiki-portfolio && adduser --system --ingroup hibiki-portfolio spring
WORKDIR /app
COPY --from=build /builds/app-main/build/libs/*.jar /app/app.jar
EXPOSE 3000:3000
USER spring:hibiki-portfolio
ENTRYPOINT ["java","-jar","/app/app.jar"]