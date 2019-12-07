package ru.rosbank.hackathon.bonusSystem.strategy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmountInterval {
    private Double from;
    private Double to;
    private Double ratio;
    private Double amount;
}
