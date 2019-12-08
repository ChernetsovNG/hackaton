package ru.rosbank.hackathon.bonusSystem.domain;

import lombok.Data;
import ru.rosbank.hackathon.bonusSystem.entity.ClientEntity;

import java.util.UUID;

@Data
public class Client {

    private UUID uuid;
    private String firstName;
    private String lastName;
    private TariffPlan tariffPlan;

    /**
     * Идентификатор тарифного плана, по которому клиенту начисляются бонусы
     */
    private UUID tariffPlanId;

    public ClientEntity toEntity() {
        ClientEntity entity = new ClientEntity();
        entity.setUuid(uuid);
        entity.setFirstName(firstName);
        entity.setLastName(lastName);
        entity.setTariffPlanId(tariffPlanId);
        entity.setTariffPlan(tariffPlan != null ? tariffPlan.toEntity() : null);
        return entity;
    }
}
