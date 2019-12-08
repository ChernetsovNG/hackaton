package ru.rosbank.hackathon.bonusSystem.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class Client {

    private UUID uuid;
    private String firstName;
    private String lastName;

    /**
     * Идентификатор тарифного плана, по которому клиенту начисляются бонусы
     */
    private UUID tariffPlanId;


    public ClientResponse toClientResponse(BigDecimal bonusAmount) {
        return new ClientResponse(this, bonusAmount);
    }
}
