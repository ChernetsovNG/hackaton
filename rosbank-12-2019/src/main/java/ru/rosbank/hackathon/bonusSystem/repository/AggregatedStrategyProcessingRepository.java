package ru.rosbank.hackathon.bonusSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.rosbank.hackathon.bonusSystem.entity.AggregatedStrategyProcessingEntity;
import ru.rosbank.hackathon.bonusSystem.entity.StrategyEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AggregatedStrategyProcessingRepository extends JpaRepository<AggregatedStrategyProcessingEntity, UUID> {

    List<AggregatedStrategyProcessingEntity> findAllByNextTimeIsLessThanEqual(OffsetDateTime time);

    boolean existsByStrategy(StrategyEntity strategyEntity);
}
