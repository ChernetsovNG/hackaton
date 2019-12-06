package ru.rosbank.hackathon.bonusSystem.strategy.impl;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.rosbank.hackathon.bonusSystem.dto.Bonus;
import ru.rosbank.hackathon.bonusSystem.dto.Transaction;
import ru.rosbank.hackathon.bonusSystem.strategy.InstantBonusesCalculateStrategy;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.UUID;

@Data
public class ThresholdInstantStrategy implements InstantBonusesCalculateStrategy {

    private final BigDecimal threshold;

    private final Double percentCoefficient;

    public ThresholdInstantStrategy(BigDecimal threshold, Double percentCoefficient) {
        this.threshold = threshold;
        this.percentCoefficient = percentCoefficient;
    }

    @Override
    public Bonus calculate(Transaction transaction) {
        BigDecimal amount = transaction.getAmount();
        if (amount.compareTo(threshold) <= 0) {
            return null;
        }
        BigDecimal bonusAmount = amount.multiply(BigDecimal.valueOf(percentCoefficient));

        Bonus bonus = new Bonus();
        bonus.setUuid(UUID.randomUUID());
        bonus.setTransactionIds(Collections.singletonList(transaction.getUuid()));
        bonus.setClientId(transaction.getClientId());
        bonus.setAmount(bonusAmount);
        bonus.setCreateTime(ZonedDateTime.now());
        bonus.setUpdateTime(null);
        bonus.setStrategyId(null);

        return bonus;
    }

    @Override
    public UUID getStrategyId() {
        return "123";
    }
}
