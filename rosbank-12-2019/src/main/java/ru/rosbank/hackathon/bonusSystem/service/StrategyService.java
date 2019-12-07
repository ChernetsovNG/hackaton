package ru.rosbank.hackathon.bonusSystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.rosbank.hackathon.bonusSystem.domain.Strategy;
import ru.rosbank.hackathon.bonusSystem.entity.AggregatedStrategyProcessingEntity;
import ru.rosbank.hackathon.bonusSystem.entity.StrategyEntity;
import ru.rosbank.hackathon.bonusSystem.repository.AggregatedStrategyProcessingRepository;
import ru.rosbank.hackathon.bonusSystem.repository.StrategyRepository;
import ru.rosbank.hackathon.bonusSystem.strategy.AggregateStrategyType;
import ru.rosbank.hackathon.bonusSystem.strategy.AggregateTimeSettings;
import ru.rosbank.hackathon.bonusSystem.strategy.StrategyType;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.rosbank.hackathon.bonusSystem.config.JsonConfig.OBJECT_MAPPER;

@Service
public class StrategyService {

    private final StrategyRepository strategyRepository;

    private final AggregatedStrategyProcessingRepository aggregatedStrategyProcessingRepository;

    private final AggregateStrategyService aggregateStrategyService;

    public StrategyService(StrategyRepository strategyRepository,
                           AggregatedStrategyProcessingRepository aggregatedStrategyProcessingRepository,
                           AggregateStrategyService aggregateStrategyService) {
        this.strategyRepository = strategyRepository;
        this.aggregatedStrategyProcessingRepository = aggregatedStrategyProcessingRepository;
        this.aggregateStrategyService = aggregateStrategyService;
    }

    @Transactional(readOnly = true)
    public List<Strategy> getAll() {
        List<StrategyEntity> allStrateries = strategyRepository.findAll();
        return allStrateries.stream()
                .map(StrategyEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Transactional
    public Strategy create(Strategy strategy) {
        StrategyEntity strategyEntity = strategy.toEntity();
        StrategyEntity created = strategyRepository.save(strategyEntity);
        // Если стратегия типа AGGREGATED, то сразу планируем её выполнение
        if (StrategyType.AGGREGATE_DATE == strategy.getType()) {
            planScheduledPerform(created, strategy.getSettings());
        }
        return created.toDomain();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void planScheduledPerform(StrategyEntity strategyEntity, String settings) {
        aggregatedStrategyProcessingRepository.save(createAggregatedStrategyProcessingEntity(strategyEntity, settings));
    }

    @Transactional
    public void linkToTariffPlan(UUID strategyId, UUID tariffPlanId) {
        strategyRepository.linkStrategyToTariffPlan(strategyId, tariffPlanId);
    }

    @Scheduled(fixedRate = 60_000L)
    @Transactional
    public void aggregatedStrategyProcess() {
        // Находим стратегии, готовые к следующему запуску
        List<AggregatedStrategyProcessingEntity> readyToStartStrategies = aggregatedStrategyProcessingRepository
                .findAllByNextTimeIsLessThanEqual(OffsetDateTime.now());
        List<AggregateStrategyType> strategies = readyToStartStrategies.stream()
                .map(AggregatedStrategyProcessingEntity::getStrategy)
                .map(StrategyEntity::getSettings)
                .map(this::convertSettingsToStrategy)
                .collect(Collectors.toList());
        List<UUID> strategyIds = readyToStartStrategies.stream()
                .map(AggregatedStrategyProcessingEntity::getStrategy)
                .map(StrategyEntity::getUuid)
                .collect(Collectors.toList());
        if (!strategies.isEmpty()) {
            List<OffsetDateTime> nextTimeList = calculateNextTime(readyToStartStrategies, strategies);
            // запускаем вычисления в отдельном потоке
            new Thread(() -> aggregateStrategyService.performAggregateStrategies(
                    strategies, strategyIds, nextTimeList)).start();
            updateNextTime(readyToStartStrategies, strategies);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateNextTime(List<AggregatedStrategyProcessingEntity> readyToStartStrategies,
                               List<AggregateStrategyType> strategies) {
        for (int i = 0; i < strategies.size(); i++) {
            AggregatedStrategyProcessingEntity strategyProcessingEntity = readyToStartStrategies.get(i);
            AggregateStrategyType strategy = strategies.get(i);
            AggregateTimeSettings timeSettings = strategy.getTimeSettings();
            OffsetDateTime toTime = timeSettings.getToTime();
            OffsetDateTime prevNextTime = strategyProcessingEntity.getNextTime();
            Integer quantity = timeSettings.getQuantity();
            int deltaMinutes = timeSettings.getTimeUnit().getMinutes();
            OffsetDateTime nextNextTime = prevNextTime.plus(quantity * deltaMinutes, ChronoUnit.MINUTES);
            if (toTime != null && nextNextTime.isAfter(toTime)) {
                aggregatedStrategyProcessingRepository.delete(strategyProcessingEntity);
                continue;
            }
            strategyProcessingEntity.setNextTime(nextNextTime);
            aggregatedStrategyProcessingRepository.save(strategyProcessingEntity);
        }
    }

    private AggregatedStrategyProcessingEntity createAggregatedStrategyProcessingEntity(StrategyEntity strategyEntity, String settings) {
        AggregateStrategyType aggregateStrategyType = convertSettingsToStrategy(settings);
        AggregateTimeSettings timeSettings = aggregateStrategyType.getTimeSettings();
        OffsetDateTime fromTime = timeSettings.getFromTime();
        Integer quantity = timeSettings.getQuantity();
        int minutes = timeSettings.getTimeUnit().getMinutes();
        OffsetDateTime nextTime = fromTime.plus(quantity * minutes, ChronoUnit.MINUTES);
        AggregatedStrategyProcessingEntity aggregatedStrategyProcessingEntity = new AggregatedStrategyProcessingEntity();
        aggregatedStrategyProcessingEntity.setUuid(UUID.randomUUID());
        aggregatedStrategyProcessingEntity.setStrategy(strategyEntity);
        aggregatedStrategyProcessingEntity.setNextTime(nextTime);
        return aggregatedStrategyProcessingEntity;
    }

    private List<OffsetDateTime> calculateNextTime(List<AggregatedStrategyProcessingEntity> readyToStartStrategies,
                                                   List<AggregateStrategyType> strategies) {
        List<OffsetDateTime> result = new ArrayList<>();
        for (int i = 0; i < strategies.size(); i++) {
            AggregatedStrategyProcessingEntity strategyProcessingEntity = readyToStartStrategies.get(i);
            AggregateStrategyType strategy = strategies.get(i);
            AggregateTimeSettings timeSettings = strategy.getTimeSettings();
            OffsetDateTime prevNextTime = strategyProcessingEntity.getNextTime();
            Integer quantity = timeSettings.getQuantity();
            int minutes = timeSettings.getTimeUnit().getMinutes();
            OffsetDateTime nextNextTime = prevNextTime.plus(quantity * minutes, ChronoUnit.MINUTES);
            result.add(nextNextTime);
        }
        return result;
    }

    private AggregateStrategyType convertSettingsToStrategy(String settings) {
        try {
            return OBJECT_MAPPER.readValue(settings, AggregateStrategyType.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
