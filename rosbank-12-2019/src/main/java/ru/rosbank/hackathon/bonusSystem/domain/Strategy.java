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

    /**
     * Конфигурация стратегии, в соответствии с которой начилсяются бонусы
     */
    private String settings;

    /**
     * Идентификатор тарифного плана, к которому относится стратегия
     */
    private UUID tariffPlanId;

    public StrategyEntity toEntity() {
        StrategyEntity entity = new StrategyEntity();
        entity.setUuid(uuid != null ? uuid : UUID.randomUUID());
        entity.setTitle(title);
        entity.setType(type.toString());
        entity.setSettings(settings);
        entity.setTariffPlanId(tariffPlanId);
        return entity;
    }
}
