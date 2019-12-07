package ru.rosbank.hackathon.bonusSystem.entity;

import lombok.Data;
import org.hibernate.annotations.Type;
import ru.rosbank.hackathon.bonusSystem.domain.TariffPlan;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "tariff_plans")
@Data
public class TariffPlanEntity {

    @Id
    @Type(type = "pg-uuid")
    @Column(name = "id")
    private UUID uuid;

    @Column(name = "title")
    private String title;

    @OneToMany(mappedBy = "tariffPlanId", fetch = FetchType.LAZY)
    private List<ClientEntity> clients;

    @OneToMany(mappedBy = "tariffPlanId", fetch = FetchType.LAZY)
    private List<StrategyEntity> strategies;

    public TariffPlan toDomain() {
        TariffPlan tariffPlan = new TariffPlan();
        tariffPlan.setTitle(title);
        tariffPlan.setUuid(uuid);
        tariffPlan.setClients(clients.stream()
                .map(ClientEntity::toDomain)
                .collect(Collectors.toList()));
        tariffPlan.setStrategies(strategies.stream()
                .map(StrategyEntity::toDomain)
                .collect(Collectors.toList()));
        return tariffPlan;
    }
}
