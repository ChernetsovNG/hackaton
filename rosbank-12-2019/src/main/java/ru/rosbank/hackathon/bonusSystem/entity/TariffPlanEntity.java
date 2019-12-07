package ru.rosbank.hackathon.bonusSystem.entity;

import lombok.Data;
import org.hibernate.annotations.Type;
import ru.rosbank.hackathon.bonusSystem.domain.TariffPlan;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

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

    public TariffPlan toDomain() {
        TariffPlan dto = new TariffPlan();
        dto.setTitle(title);
        dto.setUuid(uuid);
        return dto;
    }
}
