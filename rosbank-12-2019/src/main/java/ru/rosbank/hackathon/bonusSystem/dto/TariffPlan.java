package ru.rosbank.hackathon.bonusSystem.dto;

import lombok.Data;
import org.hibernate.annotations.Type;
import ru.rosbank.hackathon.bonusSystem.entity.StrategyEntity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.UUID;

@Data
public class TariffPlan {

    private UUID uuid;
    private String title;
    private List<Strategy> strategies;
}
