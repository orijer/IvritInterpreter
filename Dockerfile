# Use a Java JDK base image
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Install git to clone the repo
RUN apt-get update && apt-get install -y git

# Clone the full repository
RUN git clone https://github.com/orijer/IvritInterpreter.git

# Move into project directory
WORKDIR /app/IvritInterpreter

# Compile the source files
RUN mkdir -p bin && \
    find src -name "*.java" > sources.txt && \
    javac -cp "lib/*" -d bin @sources.txt

# Expose the port your Spring Boot backend uses
EXPOSE 8080

# Run the backend main class directly
CMD ["java", "-cp", "bin:lib/*", "ivrit.backend.Main"]