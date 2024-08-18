package com.example.accounting.model;

import java.util.function.Predicate;

public class PayloadPredicate extends Condition{
    private final Predicate<PaymentEntity> condition;

    public PayloadPredicate(Predicate<PaymentEntity> condition) {
        this.condition = condition;
    }

    public static PayloadPredicate condition(Predicate<PaymentEntity> condition){
        return new PayloadPredicate(condition);
    }

    @Override
    public String toString() {
        return String.format(
                "eval(%s)",
                this.condition.test(this.paymentEntity)
        );
    }
}
