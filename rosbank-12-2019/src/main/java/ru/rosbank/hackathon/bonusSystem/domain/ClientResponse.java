package ru.rosbank.hackathon.bonusSystem.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClientResponse {
    private Client client;
    private BigDecimal bonuses;

    public ClientResponse(Client client, BigDecimal bonusesAmount) {
        this.client = client;
        this.bonuses = bonusesAmount;
    }
}
