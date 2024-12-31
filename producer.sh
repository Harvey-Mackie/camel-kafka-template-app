#!/bin/bash

# Variables
KAFKA_CONTAINER_NAME=accountingservices-kafka-1       # Name of your Kafka Docker container
TOPIC="my-topic"                 # Kafka topic to produce to
MESSAGE_COUNT=100                # Number of messages to send
BROKER_PORT=9092                 # The Kafka broker port exposed by the container

# Check if the Kafka container is running
if ! docker ps --format '{{.Names}}' | grep -q "$KAFKA_CONTAINER_NAME"; then
  echo "Kafka container ($KAFKA_CONTAINER_NAME) is not running. Please start the container first."
  exit 1
fi

echo "Kafka container ($KAFKA_CONTAINER_NAME) is running."

# Produce messages to Kafka by executing the producer inside the Kafka container
for ((i=1; i<=MESSAGE_COUNT; i++))
do
  docker exec -it $KAFKA_CONTAINER_NAME \
    bash -c "echo 'Message $i' | /usr/bin/kafka-console-producer.sh --broker-list localhost:$BROKER_PORT --topic $TOPIC"
done

echo "Produced $MESSAGE_COUNT messages to topic $TOPIC."
