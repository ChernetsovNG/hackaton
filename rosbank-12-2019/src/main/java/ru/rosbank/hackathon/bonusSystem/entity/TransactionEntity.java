package ru.rosbank.hackathon.bonusSystem.entity;

import lombok.Data;
import org.hibernate.annotations.Type;
import ru.rosbank.hackathon.bonusSystem.dto.Transaction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
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

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "bonuses_transactions",
            joinColumns = {@JoinColumn(name = "transaction_uuid")},
            inverseJoinColumns = {@JoinColumn(name = "bonus_uuid")})
    List<BonusEntity> bonuses = new ArrayList<>();

    public Transaction toDto() {
        Transaction dto = new Transaction();
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
