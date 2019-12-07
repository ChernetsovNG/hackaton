package ru.rosbank.hackathon.bonusSystem.strategy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.rosbank.hackathon.bonusSystem.dto.Bonus;
import ru.rosbank.hackathon.bonusSystem.dto.Transaction;
import ru.rosbank.hackathon.bonusSystem.exception.IllegalStrategyException;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
public class InstantStrategyType {

    private List<AmountInterval> intervals;

    @JsonProperty("mcc_list")
    private List<Integer> mccList;

    @JsonProperty("min_bonus")
    private Double minBonus;

    @JsonProperty("max_bonus")
    private Double maxBonus;

    public Bonus calculateBonus(Transaction transaction, UUID strategyId) {
        // применяем ко всем MCC
        if (mccList == null) {
            return calculateBonusByIntervals(transaction, strategyId);
        } else {  // проверяем, что для нашей транзакции MCC в списке
            Integer transactionMcc = transaction.getMcc();
            if (!mccList.contains(transactionMcc)) {
                return null;
            }
            return calculateBonusByIntervals(transaction, strategyId);
        }

    }

    private Bonus calculateBonusByIntervals(Transaction transaction, UUID strategyId) {
        for (AmountInterval interval : intervals) {
            if (transactionAmountInInterval(transaction, interval)) {
                return calculateBonusByInterval(transaction, interval, strategyId);
            }
        }
        return null;
    }

    private Bonus calculateBonusByInterval(Transaction transaction, AmountInterval interval, UUID strategyId) {
        BigDecimal transactionAmount = transaction.getAmount();
        Double ratio = interval.getRatio();
        Double amount = interval.getAmount();
        BigDecimal bonusAmount = calculateBonusAmount(transactionAmount, ratio, amount);

        Bonus bonus = new Bonus();
        bonus.setUuid(UUID.randomUUID());
        bonus.setTransactionIds(Collections.singletonList(transaction.getUuid()));
        bonus.setClientId(transaction.getClientId());
        bonus.setAmount(bonusAmount);
        bonus.setCreateTime(ZonedDateTime.now());
        bonus.setUpdateTime(null);
        bonus.setStrategyId(strategyId);

        return bonus;
    }

    private BigDecimal calculateBonusAmount(BigDecimal transactionAmount, Double ratio, Double amount) {
        BigDecimal bonusAmount;
        if (ratio != null) {
            return transactionAmount.multiply(BigDecimal.valueOf(ratio));
        } else if (amount != null) {
            return transactionAmount;
        } else {
            throw new IllegalStrategyException("amount and ratio both are null");
        }
    }

    private boolean transactionAmountInInterval(Transaction transaction, AmountInterval interval) {
        BigDecimal amount = transaction.getAmount();
        Double from = interval.getFrom();
        Double to = interval.getTo();
        return to != null ?
                amount.compareTo(BigDecimal.valueOf(from)) >= 0 && amount.compareTo(BigDecimal.valueOf(to)) <= 0 :
                amount.compareTo(BigDecimal.valueOf(from)) >= 0;
    }
}
