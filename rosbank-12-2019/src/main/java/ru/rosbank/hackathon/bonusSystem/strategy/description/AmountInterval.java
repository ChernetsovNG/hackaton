package ru.rosbank.hackathon.bonusSystem.strategy.description;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmountInterval {

    /**
     * Нижняя граница интервала
     */
    private Double from;

    /**
     * Верхняя граница интервала
     */
    private Double to;

    /**
     * Процентное соотношение (коэффициент для расчёта начисляемых бонусов)
     */
    private Double ratio;

    /**
     * Конкретное заданное значение начисляемых бонусов
     */
    private Double amount;

    public boolean valueInInterval(Double value) {
        return to != null ? value >= from && value <= to : value >= from;
    }
}
