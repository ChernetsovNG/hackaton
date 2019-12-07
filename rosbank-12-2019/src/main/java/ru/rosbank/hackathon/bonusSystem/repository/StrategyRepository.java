package ru.rosbank.hackathon.bonusSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.rosbank.hackathon.bonusSystem.entity.StrategyEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StrategyRepository extends JpaRepository<StrategyEntity, UUID> {

    @Query("SELECT strategy FROM StrategyEntity strategy WHERE " +
            "strategy.type = :strategyType AND strategy.tariffPlan.uuid = :tariffPlanId")
    List<StrategyEntity> findInstantStrategiesByTariffPlan(@Param("tariffPlanId") UUID tariffPlanId,
                                                           @Param("strategyType") String type);

    @Modifying
    @Query("update StrategyEntity s set s.tariffPlanId = :tariffPlanId WHERE s.uuid = :strategyId")
    void linkStrategyToTariffPlan(@Param("strategyId") UUID strategyId, @Param("tariffPlanId") UUID tariffPlanId);
}
