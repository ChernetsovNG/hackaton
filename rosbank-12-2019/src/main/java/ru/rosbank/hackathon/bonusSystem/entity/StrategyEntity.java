package ru.rosbank.hackathon.bonusSystem.entity;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "strategies")
@Data
public class StrategyEntity {

    @Id
    @Type(type = "pg-uuid")
    @Column(name = "id")
    private UUID uuid;

    @Column(name = "title")
    private String title;

    @Column(name = "type")
    private String type;

    @Column(name = "settings")
    private String settings;

    @Column(name = "tariff_plan_id")
    private UUID tariffPlanId;

    @ManyToOne
    @JoinColumn(name = "tariff_plan_id", nullable = false, insertable = false, updatable = false)
    private TariffPlanEntity tariffPlan;
}
