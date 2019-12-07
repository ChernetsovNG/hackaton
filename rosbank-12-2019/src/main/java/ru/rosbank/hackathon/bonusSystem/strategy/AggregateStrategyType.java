package ru.rosbank.hackathon.bonusSystem.strategy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.rosbank.hackathon.bonusSystem.domain.Bonus;
import ru.rosbank.hackathon.bonusSystem.domain.Transaction;
import ru.rosbank.hackathon.bonusSystem.exception.IllegalStrategyException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
public class AggregateStrategyType {

    @JsonProperty("aggregate_time_settings")
    private AggregateTimeSettings timeSettings;

    private List<AmountInterval> intervals;

    @JsonProperty("mcc_list")
    private List<Integer> mccList;

    @JsonProperty("min_bonus")
    private Double minBonus;

    @JsonProperty("max_bonus")
    private Double maxBonus;

    public Bonus calculateBonus(Transaction transaction, UUID strategyId) {
        Bonus bonus;
        // применяем ко всем MCC
        if (mccList == null) {
            bonus = calculateBonusByIntervals(transaction, strategyId);
        } else {  // проверяем, что для нашей транзакции MCC в списке
            Integer transactionMcc = transaction.getMcc();
            if (!mccList.contains(transactionMcc)) {
                return null;
            }
            bonus = calculateBonusByIntervals(transaction, strategyId);
        }
        // проверяем ограничения на мин. и макс. значение
        if (bonus != null) {
            bonus.checkThresholdValues(minBonus, maxBonus);
        }
        return bonus;
    }

    private Bonus calculateBonusByIntervals(Transaction transaction, UUID strategyId) {
        for (AmountInterval interval : intervals) {
            if (interval.valueInInterval(transaction.getAmount().doubleValue())) {
                return calculateBonusByInterval(transaction, interval, strategyId);
            }
        }
        return null;
    }

    private Bonus calculateBonusByInterval(Transaction transaction, AmountInterval interval, UUID strategyId) {
        BigDecimal transactionAmount = transaction.getAmount();
        BigDecimal bonusAmount = calculateBonusAmount(transactionAmount, interval.getRatio(), interval.getAmount());

        Bonus bonus = new Bonus();
        bonus.setUuid(UUID.randomUUID());
        bonus.setTransactions(Collections.singletonList(transaction));
        bonus.setClientId(transaction.getClientId());
        bonus.setAmount(bonusAmount);
        bonus.setCreateTime(OffsetDateTime.now());
        bonus.setUpdateTime(OffsetDateTime.now());
        bonus.setStrategyId(strategyId);

        return bonus;
    }

    private BigDecimal calculateBonusAmount(BigDecimal transactionAmount, Double ratio, Double amount) {
        if (ratio != null) {
            return transactionAmount.multiply(BigDecimal.valueOf(ratio));
        } else if (amount != null) {
            return transactionAmount;
        } else {
            throw new IllegalStrategyException("amount and ratio both are null");
        }
    }
}
