package com.example.accounting.rules;

import com.example.accounting.model.PaymentEntity;
import com.example.accounting.model.PaymentEntity.Charge;
import com.example.accounting.model.RuleBuilder;
import com.example.accounting.model.Rule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.accounting.model.RuleBuilder.rule;

@Configuration
public class AccountingRulesConfig {

    // DEBIT Rules
    @Bean
    public Rule fxRateFeeRule() {
        return rule("FXRateFeeRule")
                .title("Apply FX Rate Fee")
                .condition(paymentEntity -> paymentEntity.getAmount() > 0 && "DEBIT".equals(paymentEntity.getTransactionType())) // Predicate for condition
                .derivationType("FX Rate Fee")
                .derivationAmount(5.0)
                .derivationDescription("Fee for FX conversion")
                .build();
    }

    @Bean
    public Rule debitChargeRule1() {
        return rule("DebitChargeRule1")
                .title("Apply Debit Charge 1")
                .condition(paymentEntity -> paymentEntity.getAmount() > 100 && "DEBIT".equals(paymentEntity.getTransactionType())) // Predicate for condition
                .derivationType("Debit Charge 1")
                .derivationAmount(2.0)
                .derivationDescription("Debit charge 1 applied")
                .build();
    }

    @Bean
    public Rule debitChargeRule2() {
        return rule("DebitChargeRule2")
                .title("Apply Debit Charge 2")
                .condition(paymentEntity -> "USD".equals(paymentEntity.getCurrency()) && "DEBIT".equals(paymentEntity.getTransactionType())) // Predicate for condition
                .derivationType("Debit Charge 2")
                .derivationAmount(3.5)
                .derivationDescription("Debit charge 2 applied")
                .build();
    }

    // CREDIT Rules
    @Bean
    public Rule serviceChargeRule() {
        return rule("ServiceChargeRule")
                .title("Apply Service Charge")
                .condition(paymentEntity -> paymentEntity.getAmount() > 0 && "CREDIT".equals(paymentEntity.getTransactionType())) // Predicate for condition
                .derivationType("Service Fee")
                .derivationAmount(10.0)
                .derivationDescription("Service charge applied")
                .build();
    }

    @Bean
    public Rule creditChargeRule1() {
        return rule("CreditChargeRule1")
                .title("Apply Credit Charge 1")
                .condition(paymentEntity -> paymentEntity.getAmount() < 100 && "CREDIT".equals(paymentEntity.getTransactionType())) // Predicate for condition
                .derivationType("Credit Charge 1")
                .derivationAmount(1.0)
                .derivationDescription("Credit charge 1 applied")
                .build();
    }

    @Bean
    public Rule creditChargeRule2() {
        return rule("CreditChargeRule2")
                .title("Apply Credit Charge 2")
                .condition(paymentEntity -> "GBP".equals(paymentEntity.getCurrency()) && "CREDIT".equals(paymentEntity.getTransactionType())) // Predicate for condition
                .derivationType("Credit Charge 2")
                .derivationAmount(4.5)
                .derivationDescription("Credit charge 2 applied")
                .build();
    }

    // Filter DEBIT rules
    @Bean
    public List<Rule> debitRules(List<Rule> allRules) {
        return allRules;
    }

    // Filter CREDIT rules
    @Bean
    public List<Rule> creditRules(List<Rule> allRules) {
        return allRules;
    }

}