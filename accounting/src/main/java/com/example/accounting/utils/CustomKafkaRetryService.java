package com.example.accounting.utils;

import com.example.accounting.exception.RetryableException;
import lombok.SneakyThrows;
import org.apache.camel.Exchange;
import org.apache.camel.component.kafka.KafkaConstants;
import org.springframework.stereotype.Component;

@Component
public class CustomKafkaRetryService {

    @SneakyThrows
    public void retry(Exchange exchange){
        Long offset = exchange.getIn().getHeader(KafkaConstants.OFFSET, Long.class);
        Integer partition = exchange.getIn().getHeader(KafkaConstants.PARTITION, Integer.class);
        String topic = exchange.getIn().getHeader(KafkaConstants.TOPIC, String.class);

        System.err.printf("Failed processing offset %d for topic %s, partition %d%n", offset, topic, partition);

        var original = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, RetryableException.class);

        Thread.sleep(2000); //Prod usage - parameterise the break period.

        throw original;
    }
}
