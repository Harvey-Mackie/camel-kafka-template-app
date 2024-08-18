package com.example.accounting.model;

import java.util.function.Predicate;

public class RuleBuilder {
    private String name;
    private String title;
    private Conditions conditions;
    private String derivationType;
    private double derivationAmount;
    private String derivationDescription;

    public static RuleBuilder rule(String name) {
        RuleBuilder builder = new RuleBuilder();
        builder.name = name;
        return builder;
    }

    public RuleBuilder title(String title) {
        this.title = title;
        return this;
    }

    public RuleBuilder condition(Predicate<PaymentEntity> predicate) {
        this.conditions = Conditions.conditions(predicate);
        return this;
    }

    public RuleBuilder derivationType(String type) {
        this.derivationType = type;
        return this;
    }

    public RuleBuilder derivationAmount(double amount) {
        this.derivationAmount = amount;
        return this;
    }

    public RuleBuilder derivationDescription(String description) {
        this.derivationDescription = description;
        return this;
    }

    public Rule build() {
        return new Rule(name, title, conditions, derivationType, derivationAmount, derivationDescription);
    }
}
