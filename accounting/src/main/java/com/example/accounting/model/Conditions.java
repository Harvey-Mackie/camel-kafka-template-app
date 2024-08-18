package com.example.accounting.model;

import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

@Getter
@Data
public class Conditions{
    private final List<Condition> ruleConditions = new ArrayList<>();

    private Conditions(Condition... ruleConditions){ this.ruleConditions.addAll(asList(ruleConditions)); }

    @SafeVarargs
    public static Conditions conditions(Predicate<PaymentEntity>... conditions){
        return streamToArray(PayloadPredicate::condition, conditions);
    }

    @SafeVarargs
    private static <T> Conditions streamToArray(Function<T, Condition> convert, T... rawValues){
        return new Conditions(stream(rawValues).map(convert).toArray(Condition[]::new));
    }

    public List<Condition> getRuleConditions(){
        return this.ruleConditions;
    }

    @Override
    public String toString(){
        return this.ruleConditions.stream().map(Condition::toString).collect(Collectors.joining("\n"));
    }
}
