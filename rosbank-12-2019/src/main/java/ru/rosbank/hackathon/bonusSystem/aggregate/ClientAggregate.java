package ru.rosbank.hackathon.bonusSystem.aggregate;

import lombok.Data;
import ru.rosbank.hackathon.bonusSystem.domain.Client;
import ru.rosbank.hackathon.bonusSystem.domain.TariffPlan;

@Data
public class ClientAggregate {

    private Client client;

    private TariffPlan tariffPlan;

    private Double bonusCount;
}
