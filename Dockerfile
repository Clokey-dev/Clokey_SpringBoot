FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file into the container
COPY ./build/libs/*.jar app.jar

# Expose the port your application will run on
EXPOSE 3306

# Command to run the JAR file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
