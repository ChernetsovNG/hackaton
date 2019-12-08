package ru.rosbank.hackathon.bonusSystem.domain;

import lombok.Data;

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
}
