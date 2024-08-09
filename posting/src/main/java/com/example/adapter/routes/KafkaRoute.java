package com.example.posting.routes;

import org.apache.camel.builder.RouteBuilder;

public class KafkaRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("kafka:posting-request-topic?brokers={{KAFKA_BROKER}}")
                .log("Message received from Kafka: ${body}")
        ;
    }
}
