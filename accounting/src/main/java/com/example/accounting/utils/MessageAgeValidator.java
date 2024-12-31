package com.example.accounting.utils;

import org.apache.camel.Exchange;
import org.apache.camel.component.kafka.KafkaConstants;
import org.springframework.stereotype.Component;

@Component
public class MessageAgeValidator {

    private static final int MAX_AGE_MINUTES = 1; // Maximum age in minutes

    public void validateMessageAge(Exchange exchange) {
        // Get the Kafka message timestamp
        Long messageTimestamp = exchange.getIn().getHeader(KafkaConstants.TIMESTAMP, Long.class);
        if (messageTimestamp == null) {
            throw new IllegalStateException("Message timestamp is missing!");
        }

        // Calculate the message age
        long currentTime = System.currentTimeMillis();
        long ageInMinutes = (currentTime - messageTimestamp) / (60 * 1000);

        // Set a header to indicate whether the message should go to DLQ
        if (ageInMinutes > MAX_AGE_MINUTES) {
            exchange.getIn().setHeader("hasRetryFailed", true);
        } else {
            exchange.getIn().setHeader("hasRetryFailed", false);
        }
    }
}
