# JAVA CHALLENGE

This repository demonstrates a microservices setup using Apache Kafka as the communication layer, where:

**shared-dto**: A library module containing shared data transfer objects (DTOs) and logging constants.

**rest**: A Spring Boot application exposing HTTP endpoints on port 8080. It sends requests via Kafka to the calculator module and awaits responses.

**calculator**: Another Spring Boot application listening on Kafka, performing calculations, and sending the results back via Kafka.

Zookeeper and Kafka (Confluent images) are included via Docker Compose, allowing you to run everything with a single command.

### HOW TO RUN

#### Prerequisites
**Git**: Version control system to clone the repository.

**Docker**: Platform to build and run containers.

**Docker** Compose: Tool to define and manage multi-container Docker applications.

#### Clone this repository:
git clone https://github.com/m7ni/Java-Challenge.git
cd Java-Challenge

Launch everything (Zookeeper, Kafka, rest, calculator) with:
**docker-compose up --build -d**

#### Check the logs:
docker-compose logs -f

Once Zookeeper, Kafka, rest, and calculator have started, you will see log messages indicating each container is running.

#### Test the REST endpoint:
Open your browser at: http://localhost:8080/calculate/sum?a=1&b=2 Or use curl: curl "http://localhost:8080/calculate/sum?a=1&b=2"

The rest module sends a request to the calculator module via Kafka, and the calculator module processes it and returns the result via Kafka.

Stop everything when you are done:
docker-compose down

### HOW IT WORKS

Zookeeper (port 2181) and Kafka (port 9092 internally) provide the messaging backbone.

The rest module (port 8080) receives HTTP requests, sends Kafka messages to the calculator module, and returns the computed results.

The calculator module (port 8081) consumes from Kafka, performs calculations, and sends responses back via Kafka.

The shared-dto module is a simple library providing DTOs like CalculationRequest, CalculationResponse, CalculationResult, and logging constants.
