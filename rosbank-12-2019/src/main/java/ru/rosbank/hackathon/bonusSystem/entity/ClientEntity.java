package ru.rosbank.hackathon.bonusSystem.entity;

import lombok.Data;
import org.hibernate.annotations.Type;
import ru.rosbank.hackathon.bonusSystem.domain.Client;
import ru.rosbank.hackathon.bonusSystem.service.BonusService;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "clients")
@Data
public class ClientEntity {

    @Id
    @Type(type = "pg-uuid")
    @Column(name = "id")
    private UUID uuid;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "tariff_plan_id")
    private UUID tariffPlanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_plan_id", nullable = false, insertable = false, updatable = false)
    private TariffPlanEntity tariffPlan;

    public Client toDomain() {
        Client client = new Client();
        client.setUuid(uuid);
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setTariffPlanId(tariffPlanId);
        client.setTariffPlan(tariffPlan.toDomain());

        return client;
    }
}
