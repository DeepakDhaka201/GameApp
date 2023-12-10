# Use the official OpenJDK 8 base image
FROM openjdk:8-jre-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/your-spring-boot-app.jar /app/app.jar

# Expose the port your application runs on
EXPOSE 8080

# Command to run your application
CMD ["java", "-jar", "app.jar"]
