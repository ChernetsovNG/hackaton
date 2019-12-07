package ru.rosbank.hackathon.bonusSystem.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class Strategy {

    private UUID uuid;

    private String title;

    private String type;

    private String settings;

    private UUID tariffPlanId;
}
