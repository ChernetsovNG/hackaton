package ru.rosbank.hackathon.bonusSystem.dto;

import lombok.Data;
import org.hibernate.annotations.Type;
import ru.rosbank.hackathon.bonusSystem.entity.TariffPlanEntity;

import java.util.UUID;

@Data
public class Client {
    private UUID uuid;

    private String firstName;

    private String lastName;

    private TariffPlan tariffPlan;

}
