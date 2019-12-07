package ru.rosbank.hackathon.bonusSystem.domain;

import lombok.Data;
import ru.rosbank.hackathon.bonusSystem.entity.StrategyEntity;
import ru.rosbank.hackathon.bonusSystem.strategy.StrategyType;

import java.util.UUID;

@Data
public class Strategy {

    private UUID uuid;
    private String title;
    private StrategyType type;
    private String settings;
    private TariffPlan tariffPlan;

    public StrategyEntity toEntity() {
        StrategyEntity entity = new StrategyEntity();
        entity.setUuid(uuid != null ? uuid : UUID.randomUUID());
        entity.setTitle(title);
        entity.setType(type.toString());
        entity.setSettings(settings);
        entity.setTariffPlanId(tariffPlan != null ? tariffPlan.getUuid() : null);
        return entity;
    }
}
