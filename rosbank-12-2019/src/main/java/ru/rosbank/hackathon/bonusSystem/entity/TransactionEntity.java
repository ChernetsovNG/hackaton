package ru.rosbank.hackathon.bonusSystem.entity;

import lombok.Data;
import org.hibernate.annotations.Type;
import ru.rosbank.hackathon.bonusSystem.dto.TransactionDto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
public class TransactionEntity {

    @Id
    @Type(type = "pg-uuid")
    @Column(name = "id")
    private UUID uuid;

    @Type(type = "pg-uuid")
    @Column(name = "client_id")
    private UUID clientId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "market_id")
    private UUID marketId;

    @Column(name = "mcc")
    private Integer mcc;

    @Column(name = "time")
    private ZonedDateTime time;

    public TransactionDto toDto() {
        TransactionDto dto = new TransactionDto();
        dto.setUuid(uuid);
        dto.setClientId(clientId);
        dto.setAmount(amount);
        dto.setCurrency(currency);
        dto.setMarketId(marketId);
        dto.setMcc(mcc);
        dto.setTime(time);
        return dto;
    }
}
