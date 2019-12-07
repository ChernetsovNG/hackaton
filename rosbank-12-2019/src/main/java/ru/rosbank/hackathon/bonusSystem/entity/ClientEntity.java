package ru.rosbank.hackathon.bonusSystem.entity;

import lombok.Data;
import org.hibernate.annotations.Type;
import ru.rosbank.hackathon.bonusSystem.dto.Client;
import ru.rosbank.hackathon.bonusSystem.dto.Transaction;

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

    @ManyToOne
    @JoinColumn(name = "tariff_plan_id", nullable = false, insertable = false, updatable = false)
    private TariffPlanEntity tariffPlan;

    public Client toDto() {
        Client dto = new Client();
        dto.setUuid(uuid);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setTariffPlan(tariffPlan.toDto());
        return dto;
    }
}
