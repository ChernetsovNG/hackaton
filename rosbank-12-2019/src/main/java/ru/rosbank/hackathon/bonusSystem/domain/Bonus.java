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
    private List<Transaction> transactions;
    private UUID clientId;
    private BigDecimal amount;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime createTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime updateTime;

    private UUID strategyId;

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
}
