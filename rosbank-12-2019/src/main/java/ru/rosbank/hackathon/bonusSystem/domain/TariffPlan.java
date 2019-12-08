package ru.rosbank.hackathon.bonusSystem.domain;

import lombok.Data;
import ru.rosbank.hackathon.bonusSystem.entity.TariffPlanEntity;

import java.util.List;
import java.util.UUID;

@Data
public class TariffPlan {

    private UUID uuid;
    private String title;
    private List<Strategy> strategies;

    public TariffPlanEntity toEntity() {
        TariffPlanEntity tariffPlanEntity = new TariffPlanEntity();
        tariffPlanEntity.setUuid(uuid != null ? uuid : UUID.randomUUID());
        tariffPlanEntity.setTitle(title);
        return tariffPlanEntity;
    }
}
