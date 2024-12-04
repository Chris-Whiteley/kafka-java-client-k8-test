# Kafka Java Client K8 Test

This project provides integration tests for Kafka producer and consumer implementations from the `cws-kafka-messaging` library. These tests validate standard Kafka messaging and chunked message processing.

## Prerequisites

Before running these tests, ensure the following prerequisites are met:

1. The [Kafka K8s](https://github.com/Chris-Whiteley/kafka-k8s) project is up and running. This project sets up Kafka v3.5.x using the Apache Kafka Raft (KRaft) protocol on an AWS (EKS)-based Kubernetes cluster.

2. A running Docker engine, such as [Docker Desktop](https://www.docker.com/products/docker-desktop), is installed and operational.

3. Kubernetes is set up and configured on your local machine, including:
    - The `kubectl` command-line tool for managing Kubernetes clusters.
    - Access to the Kubernetes cluster where Kafka is deployed.

4. Access to my AWS account to authenticate and deploy resources as required.

### Dependencies
This project relies on the following:

- **[Kafka K8s](https://github.com/Chris-Whiteley/kafka-k8s)**: Kafka cluster resources.
- **`cws-kafka-messaging` library**: Core library for producer and consumer implementations.

## Project Overview

This repository includes two integration test classes:

### 1. `KafkaIntegrationTest`

Tests the producer and consumer interaction for standard Kafka messages:
- Sends two messages: `Hello, Kafka!` and `Hello again, Kafka!`.
- Verifies message delivery and content using the consumer.

### 2. `KafkaMessageChunkingIntegrationTest`

Tests chunked message handling:
- Demonstrates the producer’s ability to split a larger message into chunks.
- Verifies the consumer’s ability to reassemble and process the complete message.

## Environment Configuration

### Required Environment Variables

| Variable          | Description                                |
|--------------------|--------------------------------------------|
| `BOOTSTRAP_SERVER` | Address of the Kafka broker cluster.       |

### Expected Output
KafkaIntegrationTest: Logs indicating successful consumption of both messages.
KafkaMessageChunkingIntegrationTest: Logs confirming successful consumption of the chunked message.

# Running the tests
Prior to running the tests build the project:
Run the following command from the project root directory to clean and build the project:
```bash
./mvnw clean install
```
## Running KafkaIntegrationTest
Follow the steps below to run the `KafkaIntegrationTest`:

### Step 1: Build the Docker Image
Use the TestDockerfile to create a Docker image for the test:
```bash
docker build -t chriswhiteley81/kafka-test -f TestDockerfile .
```
example output:
```text
[+] Building 3.6s (10/10) FINISHED
 => [internal] load build definition from TestDockerfile                                                                                          0.1s
 => => transferring dockerfile: 474B                                                                                                              0.1s
 => [internal] load metadata for docker.io/library/openjdk:21-jdk                                                                                 1.3s
 => [auth] library/openjdk:pull token for registry-1.docker.io                                                                                    0.0s
 => [internal] load .dockerignore                                                                                                                 0.0s
 => => transferring context: 2B                                                                                                                   0.0s
 => [1/4] FROM docker.io/library/openjdk:21-jdk@sha256:af9de795d1f8d3b6172f6c55ca9ba1c5768baa11bb2dc8af7045c7db9d4c33ac                           0.0s
 => [internal] load build context                                                                                                                 0.0s
 => => transferring context: 15.62kB                                                                                                              0.0s
 => CACHED [2/4] WORKDIR /app                                                                                                                     0.0s
 => [3/4] COPY ./target/kafka-java-client-k8-test-1.0.0.jar /app/kafka-java-client-k8-test-1.0.0.jar                                              0.0s
 => [4/4] COPY ./target/lib /app/lib                                                                                                              1.3s
 => exporting to image                                                                                                                            0.7s
 => => exporting layers                                                                                                                           0.7s
 => => writing image sha256:4014ce6b083b8c86c0c6dd20d8a12a2148f83f9e1a7c15d0694d52e89c60fa16                                                      0.0s
 => => naming to docker.io/chriswhiteley81/kafka-test    
```
### Step 2: Push the Docker Image
Push the Docker image to chriswhiteley81 Docker Hub repository :

```bash
docker push chriswhiteley81/kafka-test
```
example output:
```text
Using default tag: latest
The push refers to repository [docker.io/chriswhiteley81/kafka-test]
81e86ca265f3: Pushed 
d771b73f55a6: Pushed 
8ca4ac132f35: Layer already exists 
10359c5dc4ba: Layer already exists 
601b48657e0c: Layer already exists 
b42107e74152: Layer already exists 
latest: digest: sha256:f327ca49a6be0102d2c2f26418eafe976abe6726cbb6d16746a23a232d77f5df size: 1581
```
### Step 3: Deploy the Test Pod
Navigate to the Kubernetes directory and apply the kafka-test.yaml configuration file:
```bash
cd kubernetes
kubectl apply -f kafka-test.yaml
```
expected output:
```text
pod/kafka-test configured
```
### Step 4: Verify Pod Status
Check if the kafka-test pod is running or has finished:
```bash
kubectl get pods -n kafka
```
example output:
```text
NAME                  READY   STATUS      RESTARTS   AGE
kafka-0               1/1     Running     0          8h
kafka-1               1/1     Running     0          6h42m
kafka-2               1/1     Running     0          2d20h
kafka-chunking-test   0/1     Completed   0          161m
kafka-test            0/1     Completed   0          143m
```
### Step 5: Retrieve test output Log
To get the log output of the kafka-test pod:
```bash
kubectl logs -f kafka-test -n kafka > kafka_test.log
```
### Example Log Contents
Below is an example of the log output for the KafkaIntegrationTest:
```text
SLF4J(W): Class path contains multiple SLF4J providers.
SLF4J(W): Found provider [ch.qos.logback.classic.spi.LogbackServiceProvider@6e8cf4c6]
SLF4J(W): Found provider [org.slf4j.simple.SimpleServiceProvider@12edcd21]
SLF4J(W): See https://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J(I): Actual provider is of type [ch.qos.logback.classic.spi.LogbackServiceProvider@6e8cf4c6]
2024-12-04 11:04:22 [main] DEBUG c.c.m.kafka.AbstractKafkaConsumer - Subscribed to topic [myTopic]
2024-12-04 11:04:22 [main] DEBUG c.cwsoft.messaging.AbstractProducer - Producing message [MyMessage] to destination [myTopic]
2024-12-04 11:04:22 [main] DEBUG c.c.m.kafka.AbstractKafkaProducer - Sending message [MyMessage] to Kafka topic [myTopic]
...
2024-12-04 11:04:27 [main] INFO  c.c.m.k.test.KafkaIntegrationTest - First message consumed successfully: [Hello, Kafka!]
2024-12-04 11:04:27 [main] INFO  c.c.m.k.test.KafkaIntegrationTest - Second message consumed successfully: [Hello again, Kafka!]
```
## Running KafkaMessageChunkingIntegrationTest
Follow the steps below to run the `KafkaMessageChunkingIntegrationTest`:

### Step 1: Build the Docker Image
Use the TestDockerfile to create a Docker image for the test:
```bash
docker build -t chriswhiteley81/kafka-chunking-test -f ChunkingDockerfile .
```
example output:
```text
[+] Building 1.2s (10/10) FINISHED                                                                                                                docker:desktop-linux
 => [internal] load build definition from ChunkingDockerfile                                                                                                      0.0s
 => => transferring dockerfile: 510B                                                                                                                              0.0s
 => [internal] load metadata for docker.io/library/openjdk:21-jdk                                                                                                 1.0s
 => [auth] library/openjdk:pull token for registry-1.docker.io                                                                                                    0.0s
 => [internal] load .dockerignore                                                                                                                                 0.0s
 => => transferring context: 2B                                                                                                                                   0.0s
 => [1/4] FROM docker.io/library/openjdk:21-jdk@sha256:af9de795d1f8d3b6172f6c55ca9ba1c5768baa11bb2dc8af7045c7db9d4c33ac                                           0.0s
 => [internal] load build context                                                                                                                                 0.0s
 => => transferring context: 1.66kB                                                                                                                               0.0s
 => CACHED [2/4] WORKDIR /app                                                                                                                                     0.0s
 => CACHED [3/4] COPY ./target/kafka-java-client-k8-test-1.0.0.jar /app/kafka-java-client-k8-test-1.0.0.jar                                                       0.0s
 => CACHED [4/4] COPY ./target/lib /app/lib                                                                                                                       0.0s
 => exporting to image                                                                                                                                            0.0s
 => => exporting layers                                                                                                                                           0.0s
 => => writing image sha256:5484bd109c076f0ffb6fb38b27d01a6ad5d5b6fd3a16c36fcdecd92b2e1d12e8                                                                      0.0s
 => => naming to docker.io/chriswhiteley81/kafka-chunking-test     
```
### Step 2: Push the Docker Image
Push the Docker image to chriswhiteley81 Docker Hub repository :

```bash
docker push chriswhiteley81/kafka-chunking-test
```
example output:
```text
Using default tag: latest
The push refers to repository [docker.io/chriswhiteley81/kafka-chunking-test]
81e86ca265f3: Mounted from chriswhiteley81/kafka-test 
d771b73f55a6: Mounted from chriswhiteley81/kafka-test 
8ca4ac132f35: Layer already exists 
10359c5dc4ba: Layer already exists 
601b48657e0c: Layer already exists 
b42107e74152: Layer already exists 
latest: digest: sha256:daa08f7fff4e044940d6afd79eb7cbf1a6b98f4329426aad60c632a4b9f4d1fe size: 1581
```
### Step 3: Deploy the Test Pod
Navigate to the Kubernetes directory and apply the kafka-chunking-test.yaml configuration file:
```bash
cd kubernetes
kubectl apply -f kafka-chunig-test.yaml
```
expected output:
```text
pod/kafka-chunking-test configured
```
### Step 4: Verify Pod Status
Check if the kafka-test pod is running or has finished:
```bash
kubectl get pods -n kafka
```
example output:
```text
NAME                  READY   STATUS      RESTARTS   AGE
kafka-0               1/1     Running     0          8h
kafka-1               1/1     Running     0          6h42m
kafka-2               1/1     Running     0          2d20h
kafka-chunking-test   0/1     Completed   0          161m
kafka-test            0/1     Completed   0          143m
```
### Step 5: Retrieve test output Log
To get the log output of the kafka-test pod:
```bash
kubectl logs -f kafka-chunnking-test -n kafka > kafka_chunking_test.log
```
### Example Log Contents
Below is an example of the log output for the KafkaMessageChunkingIntegrationTest:
```text
SLF4J(W): Class path contains multiple SLF4J providers.
SLF4J(W): Found provider [ch.qos.logback.classic.spi.LogbackServiceProvider@6e8cf4c6]
SLF4J(W): Found provider [org.slf4j.simple.SimpleServiceProvider@12edcd21]
SLF4J(W): See https://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J(I): Actual provider is of type [ch.qos.logback.classic.spi.LogbackServiceProvider@6e8cf4c6]
2024-12-04 10:46:57 [main] DEBUG c.c.m.k.AbstractKafkaChunkingConsumer - Subscribed to topic [myChunkingTopic]
2024-12-04 10:46:57 [main] DEBUG c.cwsoft.messaging.AbstractProducer - Producing message [MyChunkingMessage] to destination [myChunkingTopic]
2024-12-04 10:46:57 [main] DEBUG c.c.m.AbstractChunkingProducer - Got 9 chunks with a total of 9 for sending to destination [myChunkingTopic]
2024-12-04 10:46:57 [main] DEBUG c.c.m.AbstractChunkingProducer - Sending chunk for message [MyChunkingMessage] with index [0] of size [10] to destination [myChunkingTopic]
2024-12-04 10:46:58 [main] DEBUG c.c.m.AbstractChunkingProducer - Sending chunk for message [MyChunkingMessage] with index [1] of size [10] to destination [myChunkingTopic]
2024-12-04 10:46:58 [main] DEBUG c.c.m.AbstractChunkingProducer - Sending chunk for message [MyChunkingMessage] with index [2] of size [10] to destination [myChunkingTopic]
2024-12-04 10:46:58 [main] DEBUG c.c.m.AbstractChunkingProducer - Sending chunk for message [MyChunkingMessage] with index [3] of size [10] to destination [myChunkingTopic]
2024-12-04 10:46:58 [main] DEBUG c.c.m.AbstractChunkingProducer - Sending chunk for message [MyChunkingMessage] with index [4] of size [10] to destination [myChunkingTopic]
2024-12-04 10:46:58 [main] DEBUG c.c.m.AbstractChunkingProducer - Sending chunk for message [MyChunkingMessage] with index [5] of size [10] to destination [myChunkingTopic]
2024-12-04 10:46:58 [main] DEBUG c.c.m.AbstractChunkingProducer - Sending chunk for message [MyChunkingMessage] with index [6] of size [10] to destination [myChunkingTopic]
2024-12-04 10:46:58 [main] DEBUG c.c.m.AbstractChunkingProducer - Sending chunk for message [MyChunkingMessage] with index [7] of size [10] to destination [myChunkingTopic]
2024-12-04 10:46:58 [main] DEBUG c.c.m.AbstractChunkingProducer - Sending chunk for message [MyChunkingMessage] with index [8] of size [4] to destination [myChunkingTopic]
2024-12-04 10:46:58 [main] DEBUG c.c.m.AbstractChunkingConsumer - Attempting to consume chunked message from source [myChunkingTopic] with timeout [PT1M]
2024-12-04 10:46:58 [main] DEBUG c.c.m.k.AbstractKafkaChunkingConsumer - Polling Kafka topic [myChunkingTopic] with timeout [PT1M]
2024-12-04 10:46:59 [kafka-producer-network-thread | producer-1] DEBUG c.c.m.k.AbstractKafkaChunkingProducer - Chunk for message [MyChunkingMessage] with index [0] successfully sent to Kafka topic [myChunkingTopic] - Partition [0], Offset [36]
2024-12-04 10:46:59 [kafka-producer-network-thread | producer-1] DEBUG c.c.m.k.AbstractKafkaChunkingProducer - Chunk for message [MyChunkingMessage] with index [1] successfully sent to Kafka topic [myChunkingTopic] - Partition [0], Offset [37]
2024-12-04 10:46:59 [kafka-producer-network-thread | producer-1] DEBUG c.c.m.k.AbstractKafkaChunkingProducer - Chunk for message [MyChunkingMessage] with index [2] successfully sent to Kafka topic [myChunkingTopic] - Partition [0], Offset [38]
2024-12-04 10:46:59 [kafka-producer-network-thread | producer-1] DEBUG c.c.m.k.AbstractKafkaChunkingProducer - Chunk for message [MyChunkingMessage] with index [3] successfully sent to Kafka topic [myChunkingTopic] - Partition [0], Offset [39]
2024-12-04 10:46:59 [kafka-producer-network-thread | producer-1] DEBUG c.c.m.k.AbstractKafkaChunkingProducer - Chunk for message [MyChunkingMessage] with index [4] successfully sent to Kafka topic [myChunkingTopic] - Partition [0], Offset [40]
2024-12-04 10:46:59 [kafka-producer-network-thread | producer-1] DEBUG c.c.m.k.AbstractKafkaChunkingProducer - Chunk for message [MyChunkingMessage] with index [5] successfully sent to Kafka topic [myChunkingTopic] - Partition [0], Offset [41]
2024-12-04 10:46:59 [kafka-producer-network-thread | producer-1] DEBUG c.c.m.k.AbstractKafkaChunkingProducer - Chunk for message [MyChunkingMessage] with index [6] successfully sent to Kafka topic [myChunkingTopic] - Partition [0], Offset [42]
2024-12-04 10:46:59 [kafka-producer-network-thread | producer-1] DEBUG c.c.m.k.AbstractKafkaChunkingProducer - Chunk for message [MyChunkingMessage] with index [7] successfully sent to Kafka topic [myChunkingTopic] - Partition [0], Offset [43]
2024-12-04 10:46:59 [kafka-producer-network-thread | producer-1] DEBUG c.c.m.k.AbstractKafkaChunkingProducer - Chunk for message [MyChunkingMessage] with index [8] successfully sent to Kafka topic [myChunkingTopic] - Partition [0], Offset [44]
2024-12-04 10:47:01 [main] DEBUG c.c.m.k.AbstractKafkaChunkingConsumer - Received message from topic [myChunkingTopic]: {"i":"1a298264-37d6-405b-805d-bb8f0f7f2180","x":0,"t":9,"n":"MyChunkingMessage","m":"{\"message\""}
2024-12-04 10:47:01 [main] DEBUG c.c.m.k.AbstractKafkaChunkingConsumer - Received message from topic [myChunkingTopic]: {"i":"1a298264-37d6-405b-805d-bb8f0f7f2180","x":1,"t":9,"n":"MyChunkingMessage","m":":\"Hello, K"}
2024-12-04 10:47:01 [main] DEBUG c.c.m.k.AbstractKafkaChunkingConsumer - Received message from topic [myChunkingTopic]: {"i":"1a298264-37d6-405b-805d-bb8f0f7f2180","x":2,"t":9,"n":"MyChunkingMessage","m":"afka! This"}
2024-12-04 10:47:01 [main] DEBUG c.c.m.k.AbstractKafkaChunkingConsumer - Received message from topic [myChunkingTopic]: {"i":"1a298264-37d6-405b-805d-bb8f0f7f2180","x":3,"t":9,"n":"MyChunkingMessage","m":" is a larg"}
2024-12-04 10:47:01 [main] DEBUG c.c.m.k.AbstractKafkaChunkingConsumer - Received message from topic [myChunkingTopic]: {"i":"1a298264-37d6-405b-805d-bb8f0f7f2180","x":4,"t":9,"n":"MyChunkingMessage","m":"er message"}
2024-12-04 10:47:01 [main] DEBUG c.c.m.k.AbstractKafkaChunkingConsumer - Received message from topic [myChunkingTopic]: {"i":"1a298264-37d6-405b-805d-bb8f0f7f2180","x":5,"t":9,"n":"MyChunkingMessage","m":" to demons"}
2024-12-04 10:47:01 [main] DEBUG c.c.m.k.AbstractKafkaChunkingConsumer - Received message from topic [myChunkingTopic]: {"i":"1a298264-37d6-405b-805d-bb8f0f7f2180","x":6,"t":9,"n":"MyChunkingMessage","m":"trate mess"}
2024-12-04 10:47:01 [main] DEBUG c.c.m.k.AbstractKafkaChunkingConsumer - Received message from topic [myChunkingTopic]: {"i":"1a298264-37d6-405b-805d-bb8f0f7f2180","x":7,"t":9,"n":"MyChunkingMessage","m":"age chunki"}
2024-12-04 10:47:01 [main] DEBUG c.c.m.k.AbstractKafkaChunkingConsumer - Received message from topic [myChunkingTopic]: {"i":"1a298264-37d6-405b-805d-bb8f0f7f2180","x":8,"t":9,"n":"MyChunkingMessage","m":"ng\"}"}
2024-12-04 10:47:01 [main] DEBUG c.c.m.AbstractChunkingConsumer - Successfully reassembled full message [MyChunkingMessage] from source [myChunkingTopic]
2024-12-04 10:47:01 [main] INFO  c.c.m.k.t.KafkaMessageChunkingIntegrationTest - Chunked message consumed successfully: [Hello, Kafka! This is a larger message to demonstrate message chunking]
2024-12-04 10:47:01 [main] INFO  c.c.m.k.t.KafkaMessageChunkingIntegrationTest - About to close producer
2024-12-04 10:47:01 [main] DEBUG c.c.m.k.AbstractKafkaChunkingProducer - Kafka producer successfully closed
2024-12-04 10:47:01 [main] INFO  c.c.m.k.t.KafkaMessageChunkingIntegrationTest - producer closed
2024-12-04 10:47:01 [main] INFO  c.c.m.k.t.KafkaMessageChunkingIntegrationTest - About to close consumer
2024-12-04 10:47:01 [main] DEBUG c.c.m.k.AbstractKafkaChunkingConsumer - Closing Kafka consumer for topic [myChunkingTopic]
2024-12-04 10:47:02 [main] INFO  c.c.m.k.t.KafkaMessageChunkingIntegrationTest - consumer closed
```








