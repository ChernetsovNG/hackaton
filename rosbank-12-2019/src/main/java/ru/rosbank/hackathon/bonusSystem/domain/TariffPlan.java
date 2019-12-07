package ru.rosbank.hackathon.bonusSystem.domain;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class TariffPlan {

    private UUID uuid;
    private String title;
    private List<Strategy> strategies;
}
