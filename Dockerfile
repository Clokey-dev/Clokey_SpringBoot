FROM openjdk:17-jdk-slim

# Set environment variables
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV DB_URL=${DB_URL}

# Set the working directory
WORKDIR /app

# Copy the built JAR file into the container
COPY ./build/libs/*.jar app.jar

# Expose the port your application will run on
EXPOSE 8080

# Command to run the JAR file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
