# Giai đoạn build
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copy file POM trước để tận dụng cache dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline

# Copy toàn bộ source code và build
COPY src ./src
RUN mvn clean package -DskipTests

# Giai đoạn chạy
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/nhatrobackend-0.0.1-SNAPSHOT.jar app.jar

# Mở cổng 8080
EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]