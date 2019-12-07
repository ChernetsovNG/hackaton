package ru.rosbank.hackathon.bonusSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rosbank.hackathon.bonusSystem.entity.AggregatedStrategyProcessingEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface AggregatedStrategyProcessingRepository extends JpaRepository<AggregatedStrategyProcessingEntity, UUID> {

    List<AggregatedStrategyProcessingEntity> findAllByNextTimeIsLessThanEqual(OffsetDateTime time);
}
