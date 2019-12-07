package ru.rosbank.hackathon.bonusSystem.strategy;

import lombok.Data;

@Data
public class AmountInterval {
    private Double from;
    private Double to;
    private Double ratio;
    private Double amount;
}
