package com.example.accounting.routes;

import com.example.accounting.model.PaymentEntity;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.accounting.rules.RulesEngineService;

import java.util.ArrayList;

@Component
public class KafkaRoute extends RouteBuilder {

    @Autowired
    private RulesEngineService rulesEngineService;

    @Override
    public void configure() {
        from("kafka:accounting-request-topic?brokers=localhost:9092")
                .routeId("processing-accounting-rules")
                .doTry()
                .unmarshal().json(JsonLibrary.Jackson, PaymentEntity.class)
                .log("Message received from Kafka: ${body}")
                .process(exchange -> {
                    PaymentEntity entity = exchange.getIn().getBody(PaymentEntity.class);

                    if (entity.getCharges() == null) {
                        entity.setCharges(new ArrayList<>());
                    }

                    // Apply all rules to the entity
                    PaymentEntity modifiedEntity = rulesEngineService.applyAllRules(entity);

                    exchange.getIn().setBody(modifiedEntity);
                })
                .log("Modified PaymentEntity after applying rules: ${body}")
                .to("kafka:posting-request-topic?brokers=localhost:9092")
                .doCatch(Exception.class)
                .log("Error processing message: ${exception.message}")
                .end();
    }
}

