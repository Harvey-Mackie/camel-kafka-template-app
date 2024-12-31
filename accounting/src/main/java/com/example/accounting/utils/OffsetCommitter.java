package com.example.accounting.utils;

import com.example.accounting.exception.RetryableException;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.component.kafka.consumer.KafkaManualCommit;

import static org.apache.camel.Exchange.OFFSET;
import static org.apache.camel.component.kafka.KafkaConstants.MANUAL_COMMIT;

public class OffsetCommitter {
    // This is the method which should be used for PROD - simply remove references to shouldPass then delete commitAndFail commitAndPass and route everything to this method from routes.
    public static void manuallyCommit(
            @Header(MANUAL_COMMIT) KafkaManualCommit manualCommit,
            @Header("kafka." + OFFSET) Long offset,
            Exchange exchange,
            boolean shouldPass //testing value - remove for actual implementation - added to simulate failures with the flag.
    ) {
        if (manualCommit != null) {
            try {
                //testing method - not intended for prod usage...
                if(!shouldPass){
                    throw new RetryableException("Failed to commit offset (executed in testing mode with should pass = false) - " + offset);
                }

                manualCommit.commit();
                System.out.println("Offset committed successfully. + " + offset + "");
            } catch (Exception e) {
                throw new RetryableException("Failed to commit offset: " + e.getMessage());
            }
        } else {
            throw new IllegalStateException("Kafka manual commit object is missing from headers...potential issue with route configuration");
        }
    }

    /* Methods outlined below are for testing purposes only (simulate events) */
    public static void commitAndFail(
            @Header(MANUAL_COMMIT) KafkaManualCommit manualCommit,
            @Header(OFFSET) Long offset,
            Exchange exchange
    ) {
        OffsetCommitter.manuallyCommit(manualCommit, offset, exchange, false);
    }

    public static void commitAndPass(
            @Header(MANUAL_COMMIT) KafkaManualCommit manualCommit,
            @Header(OFFSET) Long offset,
            Exchange exchange
    ){
        OffsetCommitter.manuallyCommit(manualCommit, offset, exchange, true);
    }


}
