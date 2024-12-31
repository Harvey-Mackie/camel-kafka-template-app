#!/bin/bash

# Kafka broker details
BROKER="localhost:9092"
TOPIC="accounting-request-topic"

# Number of messages to send
NUM_MESSAGES=100

# Function to produce messages
produce_messages() {
  for ((i=1;i<=NUM_MESSAGES;i++))
  do
    echo "Message $i" | kafka-console-producer.sh --broker-list $BROKER --topic $TOPIC > /dev/null 2>&1
    if (( $i % 10 == 0 )); then
      echo "Produced $i messages so far..."
    fi
  done
  echo "Finished producing $NUM_MESSAGES messages."
}

# Call the function
produce_messages

