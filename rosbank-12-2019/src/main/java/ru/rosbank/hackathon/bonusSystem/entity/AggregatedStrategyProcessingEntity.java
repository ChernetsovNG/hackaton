package ru.rosbank.hackathon.bonusSystem.entity;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "aggregated_strategy_processing")
@Data
public class AggregatedStrategyProcessingEntity {

    @Id
    @Type(type = "pg-uuid")
    @Column(name = "id")
    private UUID uuid;

    @OneToOne(fetch = FetchType.EAGER)
    private StrategyEntity strategy;

    @Column(name = "next_time")
    private OffsetDateTime nextTime;
}
