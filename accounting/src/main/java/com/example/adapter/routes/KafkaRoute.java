package com.example.accounting.routes;

import org.apache.camel.builder.RouteBuilder;

public class KafkaRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("kafka:accounting-request-topic?brokers={{KAFKA_BROKER}}")
                .log("Message received from Kafka: ${body}")
                .to("kafka:posting-request-topic?brokers={{KAFKA_BROKER}}")
        ;
    }
}
