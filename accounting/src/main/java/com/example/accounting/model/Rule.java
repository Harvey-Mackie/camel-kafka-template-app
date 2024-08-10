package com.example.accounting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Rule {
    private String name;              // The name of the rule
    private String title;             // The title of the rule
    private String conditions;        // The conditions that trigger the rule

    private String derivationType;    // The type of charge to be applied
    private double derivationAmount;  // The amount of the charge
    private String derivationDescription; // The description of the charge
}
