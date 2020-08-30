package ru.rosbank.hackathon.bonusSystem.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.rosbank.hackathon.bonusSystem.domain.Strategy;
import ru.rosbank.hackathon.bonusSystem.entity.AggregatedStrategyProcessingEntity;
import ru.rosbank.hackathon.bonusSystem.entity.StrategyEntity;
import ru.rosbank.hackathon.bonusSystem.repository.AggregatedStrategyProcessingRepository;
import ru.rosbank.hackathon.bonusSystem.repository.StrategyRepository;
import ru.rosbank.hackathon.bonusSystem.strategy.description.AggregateStrategy;
import ru.rosbank.hackathon.bonusSystem.strategy.description.AggregateTimeSettings;
import ru.rosbank.hackathon.bonusSystem.strategy.enums.StrategyType;

import javax.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.rosbank.hackathon.bonusSystem.strategy.description.AggregateStrategy.convertSettingsToStrategy;

@Service
public class StrategyService {

    private final StrategyRepository strategyRepository;

    private final AggregatedStrategyProcessingRepository aggregatedStrategyProcessingRepository;

    public StrategyService(StrategyRepository strategyRepository,
                           AggregatedStrategyProcessingRepository aggregatedStrategyProcessingRepository) {
        this.strategyRepository = strategyRepository;
        this.aggregatedStrategyProcessingRepository = aggregatedStrategyProcessingRepository;
    }

    @Transactional(readOnly = true)
    public List<Strategy> getAll() {
        List<StrategyEntity> allStrateries = strategyRepository.findAll();
        return allStrateries.stream()
                .map(StrategyEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Strategy get(UUID id) {
        return strategyRepository.findById(id)
                .map(StrategyEntity::toDomain)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Strategy create(Strategy strategy) {
        StrategyEntity strategyEntity = strategy.toEntity();
        StrategyEntity created = strategyRepository.save(strategyEntity);
        // Если стратегия типа AGGREGATED, то сразу после создания планируем её выполнение
        if (StrategyType.AGGREGATE_DATE == strategy.getType()) {
            planScheduledPerform(created, strategy.getSettings());
        }
        return created.toDomain();
    }

    @Scheduled(fixedRate = 60_000L)
    @Transactional
    public void planningAggregateStrategy() {
        List<StrategyEntity> aggregateStrategies = strategyRepository.findAllByType("AGGREGATE_DATE");
        aggregateStrategies.forEach(strategyEntity -> {
            // если она ещё не была запланирована, то планируем
            if (!aggregatedStrategyProcessingRepository.existsByStrategy(strategyEntity)) {
                planScheduledPerform(strategyEntity, strategyEntity.getSettings());
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void planScheduledPerform(StrategyEntity strategyEntity, String settings) {
        aggregatedStrategyProcessingRepository.save(createAggregatedStrategyProcessingEntity(strategyEntity, settings));
    }

    @Transactional
    public void linkToTariffPlan(UUID strategyId, UUID tariffPlanId) {
        strategyRepository.linkStrategyToTariffPlan(strategyId, tariffPlanId);
    }

    private AggregatedStrategyProcessingEntity createAggregatedStrategyProcessingEntity(StrategyEntity strategyEntity, String settings) {
        AggregatedStrategyProcessingEntity aggregatedStrategyProcessingEntity = new AggregatedStrategyProcessingEntity();
        aggregatedStrategyProcessingEntity.setUuid(UUID.randomUUID());
        aggregatedStrategyProcessingEntity.setStrategy(strategyEntity);
        aggregatedStrategyProcessingEntity.setNextTime(calculateNextRunTime(settings));
        return aggregatedStrategyProcessingEntity;
    }

    private OffsetDateTime calculateNextRunTime(String settings) {
        AggregateStrategy aggregateStrategy = convertSettingsToStrategy(settings);
        AggregateTimeSettings timeSettings = aggregateStrategy.getTimeSettings();
        OffsetDateTime fromTime = timeSettings.getFromTime();
        Integer quantity = timeSettings.getQuantity();
        int minutes = timeSettings.getTimeUnit().getMinutes();
        return fromTime.plus(quantity * minutes, ChronoUnit.MINUTES);
    }

    @Transactional
    public Strategy update(Strategy strategy) {
        StrategyEntity strategyEntity = strategy.toEntity();
        StrategyEntity updatedEntity = strategyRepository.save(strategyEntity);
        return updatedEntity.toDomain();
    }

    @Transactional
    public void deleteById(final UUID uuid) {
        strategyRepository.deleteById(uuid);
    }
}
