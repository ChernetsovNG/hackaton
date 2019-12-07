package ru.rosbank.hackathon.bonusSystem.entity;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bonuses")
@Data
public class BonusEntity {

    @Id
    @Type(type = "pg-uuid")
    @Column(name = "id")
    private UUID uuid;

    @Type(type = "pg-uuid")
    @Column(name = "client_id")
    private UUID clientId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "create_time")
    private OffsetDateTime createTime;

    @Column(name = "update_time")
    private OffsetDateTime updateTime;

    @Type(type = "pg-uuid")
    @Column(name = "strategy_id")
    private UUID strategyId;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "bonuses_transactions",
            joinColumns = {@JoinColumn(name = "bonus_uuid")},
            inverseJoinColumns = {@JoinColumn(name = "transaction_uuid")})
    List<TransactionEntity> transactions = new ArrayList<>();
}
