package ru.rosbank.hackathon.bonusSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.rosbank.hackathon.bonusSystem.entity.TariffPlanEntity;

import java.util.UUID;

@Repository
public interface TariffPlanRepository extends JpaRepository<TariffPlanEntity, UUID> {
}
