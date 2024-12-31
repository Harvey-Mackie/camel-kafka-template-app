package com.example.accounting.routes;

import com.example.accounting.exception.RetryableException;
import com.example.accounting.model.DRLStyle;
import com.example.accounting.model.PaymentEntity;
import com.example.accounting.utils.CustomKafkaRetryService;
import com.example.accounting.utils.MessageAgeValidator;
import com.example.accounting.utils.OffsetCommitter;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.accounting.rules.RulesEngineService;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class KafkaRoute extends RouteBuilder {

    @Autowired
    private RulesEngineService rulesEngineService;

    @Autowired
    private CustomKafkaRetryService customKafkaRetryService;

    private String memoryUsageFilePath = "results.txt";

    @Override
    public void configure() {


        from("kafka:accounting-request-topic?" +
                "brokers=localhost:9092" +
                "&groupId=my-consumer-group" +
                "&autoCommitEnable=false" +
                "&allowManualCommit=true" +
                "&autoOffsetReset=earliest" +
                "&maxPollIntervalMs=40000" +
                "&sessionTimeoutMs=20000" +
                "&maxPollRecords=1" +
                "&isolationLevel=read_uncommitted" +
                "&breakOnFirstError=true" +
                "&pollOnError=RECONNECT")
            .routeId("processing-accounting-rules")
            .bean(MessageAgeValidator.class, "validateMessageAge") // Validate the message age (if > 1 mins it's failed)

            .choice()
                .when(header("hasRetryFailed").isEqualTo(true))
                    .log("Message older than 1 minutes. Routing to DLQ (in-future still to-do).")
                    .bean(OffsetCommitter.class, "commitAndPass")
                    //TODO - Navigate to a DLQ or ERROR topic
                    .stop()

            .otherwise()
                .doTry()
                    .unmarshal().json(JsonLibrary.Jackson, PaymentEntity.class)
                    .log("Message received from Kafka: ${body}")
                    .process(this::processAndTrackMemory)
                    .bean(OffsetCommitter.class, "commitAndFail")
                    .log("Modified PaymentEntity after applying rules: ${body}")
                    .to("kafka:posting-request-topic?brokers=localhost:9092")
                    .log("Sent")

                .doCatch(RetryableException.class)
                    .log("Error processing message: ${exception.message}")
                    .process(customKafkaRetryService::retry) //seek *was* operation applied here ⬅
                .end();
    }




    //util methods...
    private void processAndTrackMemory(Exchange exchange) {
        PaymentEntity entity = exchange.getIn().getBody(PaymentEntity.class);

        if (entity.getCharges() == null) {
            entity.setCharges(new ArrayList<>());
        }

        var style = DRLStyle.NO_DROOLS;

        System.gc();

        // Track memory usage before applying rules
        long memoryBefore = getMemoryUsage();

        // Apply rules
        PaymentEntity modifiedEntity = rulesEngineService.applyAllRules(entity, style);

        // Track memory usage after applying rules
        long memoryAfter = getMemoryUsage();
        long memoryUsed = memoryAfter - memoryBefore;

        // Save memory usage to file
        saveMemoryUsageToFile(style, memoryUsed);

        exchange.getIn().setBody(modifiedEntity);
    }

    // Helper method to calculate memory usage
    private long getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    // Save memory usage data to a file
    private void saveMemoryUsageToFile(DRLStyle drlStyle, long memoryUsed) {
        try (FileWriter writer = new FileWriter(memoryUsageFilePath, true)) {  // Append mode
            writer.write("DRLStyle." + drlStyle.name() + " Memory used: " + memoryUsed + " bytes\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

