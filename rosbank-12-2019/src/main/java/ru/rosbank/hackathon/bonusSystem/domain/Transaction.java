package ru.rosbank.hackathon.bonusSystem.domain;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.rosbank.hackathon.bonusSystem.entity.TransactionEntity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class Transaction {

    private UUID uuid;
    private UUID clientId;

    /**
     * Сумма транзакция
     */
    private BigDecimal amount;

    /**
     * Валюта транзакции
     * // TODO: 07.12.2019 пока что не учитывается при вычислениях (считатеся, что это всегда рубли)
     */
    private String currency;

    /**
     * Идентификатор торговой точки
     */
    private UUID marketId;

    /**
     * Идентификатор категории тогровой точки
     */
    private Integer mcc;

    /**
     * Время выполнения транзакции
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime time;

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
