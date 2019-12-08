package ru.rosbank.hackathon.bonusSystem.domain;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.rosbank.hackathon.bonusSystem.entity.BonusEntity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class Bonus {

    private UUID uuid;

    /**
     * Список транзакций, по которым начислен бонус
     */
    private List<Transaction> transactions;

    /**
     * Идентификато клиента, для которого начислен бонус
     */
    private UUID clientId;

    /**
     * Количество бонусов (в условных единицах)
     */
    private BigDecimal amount;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime createTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime updateTime;

    /**
     * Идентификатор стратегии, по которой начислен бонус
     */
    private UUID strategyId;

    private Strategy strategy;

    public BonusEntity toEntity() {
        BonusEntity entity = new BonusEntity();
        entity.setUuid(uuid);
        entity.setClientId(clientId);
        entity.setAmount(amount);
        entity.setCreateTime(createTime);
        entity.setUpdateTime(updateTime);
        entity.setStrategyId(strategyId);
        entity.setTransactions(transactions.stream()
                .map(Transaction::toEntity)
                .collect(Collectors.toList()));
        return entity;
    }

    public void checkThresholdValues(Double minBonus, Double maxBonus) {
        if (minBonus != null && maxBonus == null) {
            if (amount.compareTo(BigDecimal.valueOf(minBonus)) < 0) {
                setAmount(BigDecimal.valueOf(minBonus));
            }
        } else if (minBonus == null) {
            if (amount.compareTo(BigDecimal.valueOf(maxBonus)) > 0) {
                setAmount(BigDecimal.valueOf(maxBonus));
            }
        } else {
            if (amount.compareTo(BigDecimal.valueOf(minBonus)) < 0) {
                setAmount(BigDecimal.valueOf(minBonus));
            }
            if (amount.compareTo(BigDecimal.valueOf(maxBonus)) > 0) {
                setAmount(BigDecimal.valueOf(maxBonus));
            }
        }
    }
}
