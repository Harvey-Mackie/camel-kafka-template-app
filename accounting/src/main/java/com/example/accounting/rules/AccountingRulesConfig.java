package com.example.accounting.rules;

import com.example.accounting.model.PaymentEntity;
import com.example.accounting.model.PaymentEntity.Charge;
import com.example.accounting.model.Rule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountingRulesConfig {

    public static Rule.RuleBuilder rule(String name){
        return Rule.builder().name(name);
    }

    @Bean
    public Rule fxRateFeeRule() {
        return rule("FXRateFeeRule")
                .title("Apply FX Rate Fee")
                .conditions("$entity: PaymentEntity(amount > 0, transactionType == 'DEBIT')")
                .derivationType("FX Rate Fee")
                .derivationAmount(5.0)
                .derivationDescription("Fee for FX conversion")
                .build();
    }

    @Bean
    public Rule serviceChargeRule() {
        return rule("ServiceChargeRule")
                .title("Apply Service Charge")
                .conditions("$entity: PaymentEntity(amount > 0, transactionType == 'DEBIT')")
                .derivationType("Service Fee")
                .derivationAmount(10.0)
                .derivationDescription("Service charge applied")
                .build();
    }

}