package ru.rosbank.hackathon.bonusSystem.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.rosbank.hackathon.bonusSystem.entity.TransactionEntity;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class Transaction {

    private UUID uuid;
    private UUID clientId;
    private BigDecimal amount;
    private String currency;
    private UUID marketId;
    private Integer mcc;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime time;

    public TransactionEntity toEntity() {
        TransactionEntity entity = new TransactionEntity();
        entity.setUuid(uuid);
        entity.setClientId(clientId);
        entity.setAmount(amount);
        entity.setCurrency(currency);
        entity.setMarketId(marketId);
        entity.setMcc(mcc);
        entity.setTime(time);
        return entity;
    }
}
