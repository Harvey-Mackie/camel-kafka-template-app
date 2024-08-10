package com.example.accounting.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEntity {
    private String transactionType;
    private String transactionId;
    private String customerId;
    private double amount;
    private String currency;
    private String description;
    private List<Notification> notifications;
    private List<Charge> charges;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Notification {
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Charge {
        private String type;
        private double amount;
        private String description;
    }
}