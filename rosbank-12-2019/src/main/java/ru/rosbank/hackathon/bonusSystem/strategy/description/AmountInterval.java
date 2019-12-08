package ru.rosbank.hackathon.bonusSystem.strategy.description;

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

    public boolean valueInInterval(Double value) {
        return to != null ? value >= from && value <= to : value >= from;
    }
}
