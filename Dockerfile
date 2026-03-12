# Use Amazon Corretto 21 as the base image for Java 21
FROM amazoncorretto:21-alpine

# Set the working directory
WORKDIR /app

# Copy the built jar file from the target directory
# Note: This assumes you've run 'mvn clean package'
COPY target/*.jar app.jar

# Expose the port the service runs on (standard for shipping service)
EXPOSE 8084

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
