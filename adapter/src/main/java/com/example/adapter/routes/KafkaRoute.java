package com.example.adapter.routes;

import org.apache.camel.builder.RouteBuilder;

public class KafkaRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("kafka:request-topic?brokers={{KAFKA_BROKER}}")
                .log("Message received from Kafka: ${body}");
    }
}
