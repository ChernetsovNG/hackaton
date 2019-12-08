package ru.rosbank.hackathon.bonusSystem.strategy.calc;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.rosbank.hackathon.bonusSystem.domain.Bonus;
import ru.rosbank.hackathon.bonusSystem.domain.Transaction;
import ru.rosbank.hackathon.bonusSystem.entity.AggregatedStrategyProcessingEntity;
import ru.rosbank.hackathon.bonusSystem.entity.BonusEntity;
import ru.rosbank.hackathon.bonusSystem.entity.StrategyEntity;
import ru.rosbank.hackathon.bonusSystem.entity.TransactionEntity;
import ru.rosbank.hackathon.bonusSystem.exception.IllegalStrategyException;
import ru.rosbank.hackathon.bonusSystem.exception.NotImplementedException;
import ru.rosbank.hackathon.bonusSystem.repository.AggregatedStrategyProcessingRepository;
import ru.rosbank.hackathon.bonusSystem.repository.BonusRepository;
import ru.rosbank.hackathon.bonusSystem.repository.TransactionRepository;
import ru.rosbank.hackathon.bonusSystem.strategy.description.AggregateStrategy;
import ru.rosbank.hackathon.bonusSystem.strategy.description.AggregateTimeSettings;
import ru.rosbank.hackathon.bonusSystem.strategy.description.AmountInterval;
import ru.rosbank.hackathon.bonusSystem.strategy.enums.AggregateFunction;
import ru.rosbank.hackathon.bonusSystem.tuple.Pair;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AggregateBonusesCalculateStrategyImpl implements BonusesCalculateStrategy {

    private final AggregatedStrategyProcessingRepository aggregatedStrategyProcessingRepository;

    private final TransactionRepository transactionRepository;

    private final BonusRepository bonusRepository;

    public AggregateBonusesCalculateStrategyImpl(AggregatedStrategyProcessingRepository aggregatedStrategyProcessingRepository,
                                                 TransactionRepository transactionRepository, BonusRepository bonusRepository) {
        this.aggregatedStrategyProcessingRepository = aggregatedStrategyProcessingRepository;
        this.transactionRepository = transactionRepository;
        this.bonusRepository = bonusRepository;
    }

    @Scheduled(fixedRate = 60_000L)
    @Transactional
    public void aggregatedStrategyProcess() {
        // Находим стратегии, готовые к следующему запуску
        List<AggregatedStrategyProcessingEntity> readyToStartStrategies = aggregatedStrategyProcessingRepository
                .findAllByNextTimeIsLessThanEqual(OffsetDateTime.now());
        List<AggregateStrategy> strategies = readyToStartStrategies.stream()
                .map(AggregatedStrategyProcessingEntity::getStrategy)
                .map(StrategyEntity::getSettings)
                .map(AggregateStrategy::convertSettingsToStrategy)
                .collect(Collectors.toList());
        List<UUID> strategyIds = readyToStartStrategies.stream()
                .map(AggregatedStrategyProcessingEntity::getStrategy)
                .map(StrategyEntity::getUuid)
                .collect(Collectors.toList());
        if (!strategies.isEmpty()) {
            List<OffsetDateTime> nextTimeList = calculateNextTime(readyToStartStrategies, strategies);
            // запускаем вычисления в отдельном потоке
            new Thread(() -> performAggregateStrategies(strategies, strategyIds, nextTimeList)).start();
            updateNextTime(readyToStartStrategies, strategies);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateNextTime(List<AggregatedStrategyProcessingEntity> readyToStartStrategies,
                               List<AggregateStrategy> strategies) {
        for (int i = 0; i < strategies.size(); i++) {
            AggregatedStrategyProcessingEntity strategyProcessingEntity = readyToStartStrategies.get(i);
            AggregateStrategy strategy = strategies.get(i);
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

    private List<OffsetDateTime> calculateNextTime(List<AggregatedStrategyProcessingEntity> readyToStartStrategies,
                                                   List<AggregateStrategy> strategies) {
        List<OffsetDateTime> result = new ArrayList<>();
        for (int i = 0; i < strategies.size(); i++) {
            AggregatedStrategyProcessingEntity strategyProcessingEntity = readyToStartStrategies.get(i);
            AggregateStrategy strategy = strategies.get(i);
            AggregateTimeSettings timeSettings = strategy.getTimeSettings();
            OffsetDateTime prevNextTime = strategyProcessingEntity.getNextTime();
            Integer quantity = timeSettings.getQuantity();
            int minutes = timeSettings.getTimeUnit().getMinutes();
            OffsetDateTime nextNextTime = prevNextTime.plus(quantity * minutes, ChronoUnit.MINUTES);
            result.add(nextNextTime);
        }
        return result;
    }

    public void performAggregateStrategies(List<AggregateStrategy> aggregateStrategies, List<UUID> strategyIds,
                                           List<OffsetDateTime> nextTimeList) {
        int count = aggregateStrategies.size();
        for (int i = 0; i < count; i++) {
            AggregateStrategy strategy = aggregateStrategies.get(i);
            UUID strategyId = strategyIds.get(i);
            OffsetDateTime nextTime = nextTimeList.get(i);
            AggregateTimeSettings timeSettings = strategy.getTimeSettings();
            Integer quantity = timeSettings.getQuantity();
            int minutes = timeSettings.getTimeUnit().getMinutes();
            OffsetDateTime toTime = nextTime;
            OffsetDateTime fromTime = toTime.minus(quantity * minutes, ChronoUnit.MINUTES);
            performAggregateStrategy(strategy, strategyId, fromTime, toTime);
        }
    }

    @Transactional
    public void performAggregateStrategy(AggregateStrategy strategy, UUID strategyId,
                                         OffsetDateTime fromTime, OffsetDateTime toTime) {
        // выбираем транзакции в заданном временном диапазоне
        List<TransactionEntity> transactions = transactionRepository
                .findAllByTimeGreaterThanEqualAndTimeLessThanEqual(fromTime, toTime);
        if (transactions.isEmpty()) {
            return;
        }
        // группируем их по пользователю
        Map<UUID, List<Transaction>> domainTransactionByUsersMap = groupTransactionsByClients(transactions);
        domainTransactionByUsersMap.forEach((clientId, domainTransactions) -> {
            Bonus bonus = calculateBonus(clientId, domainTransactions, strategy, strategyId);
            BonusEntity bonusEntity = bonus.toEntity();
            bonusRepository.save(bonusEntity);
        });
    }

    private Map<UUID, List<Transaction>> groupTransactionsByClients(List<TransactionEntity> transactions) {
        Map<UUID, List<TransactionEntity>> transactionByUsersMap = transactions.stream()
                .collect(Collectors.groupingBy(TransactionEntity::getClientId));
        // для каждого пользователя считаем сумму по его тразакциям
        return transactionByUsersMap.entrySet().stream()
                .map(entry -> {
                    UUID clientId = entry.getKey();
                    List<TransactionEntity> clientTransactions = entry.getValue();
                    List<Transaction> domainTransactions = clientTransactions.stream()
                            .map(TransactionEntity::toDomain)
                            .collect(Collectors.toList());
                    return Pair.of(clientId, domainTransactions);
                })
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    private Bonus calculateBonus(UUID clientId, List<Transaction> transactions,
                                 AggregateStrategy strategy, UUID strategyId) {
        List<Integer> mccList = strategy.getMccList();
        List<AmountInterval> intervals = strategy.getIntervals();
        AggregateFunction aggregateFunction = strategy.getAggregateFunction();
        Double minBonus = strategy.getMinBonus();
        Double maxBonus = strategy.getMaxBonus();
        Long bonusMaxAgeMs = strategy.getBonusMaxAgeMs();
        Bonus bonus;
        if (mccList == null) {  // применяем ко всем MCC
            bonus = calculateBonusByIntervals(transactions, aggregateFunction, intervals, clientId, strategyId);
        } else {  // фильтруем список транзакций по применимым к ним MCC
            transactions = transactions.stream()
                    .filter(transaction -> mccList.contains(transaction.getMcc()))
                    .collect(Collectors.toList());
            bonus = calculateBonusByIntervals(transactions, aggregateFunction, intervals, clientId, strategyId);
        }
        // проверяем ограничения на мин. и макс. значение
        if (bonus != null) {
            bonus.checkThresholdValues(minBonus, maxBonus);
        }
        if (bonus != null && bonusMaxAgeMs != null) {
            bonus.setTimeToLive(bonus.getCreateTime().plus(bonusMaxAgeMs, ChronoUnit.MILLIS));
        }
        return bonus;
    }

    private Bonus calculateBonusByIntervals(List<Transaction> transactions, AggregateFunction aggregateFunction,
                                            List<AmountInterval> intervals, UUID clientId, UUID strategyId) {
        // в зависимости от агрегирующей функции считаем либо сумму, либо количество транзакций за период
        double comparisonValue = calculateComparisonValue(aggregateFunction, transactions);
        for (AmountInterval interval : intervals) {
            if (interval.valueInInterval(comparisonValue)) {
                return calculateBonusByInterval(transactions, comparisonValue, interval, clientId, strategyId);
            }
        }
        return null;
    }

    private Bonus calculateBonusByInterval(List<Transaction> transactions, Double comparisonValue,
                                           AmountInterval interval, UUID clientId, UUID strategyId) {
        Bonus bonus = new Bonus();
        bonus.setUuid(UUID.randomUUID());
        bonus.setTransactions(transactions);
        bonus.setClientId(clientId);
        bonus.setAmount(calculateBonusAmount(comparisonValue, interval.getRatio(), interval.getAmount()));
        bonus.setCreateTime(OffsetDateTime.now());
        bonus.setUpdateTime(OffsetDateTime.now());
        bonus.setStrategyId(strategyId);
        return bonus;
    }

    private double calculateComparisonValue(AggregateFunction aggregateFunction, List<Transaction> transactions) {
        Stream<Transaction> stream = transactions.stream();
        switch (aggregateFunction) {
            case SUM:
                return stream
                        .map(Transaction::getAmount)
                        .map(BigDecimal::doubleValue)
                        .mapToDouble(v -> v)
                        .sum();
            case COUNT:
                return stream.count();
            default:
                throw new NotImplementedException("AggregateFunction = " + aggregateFunction + " not implemented yet");
        }
    }

    private BigDecimal calculateBonusAmount(Double comparisonValue, Double ratio, Double amount) {
        if (ratio != null) {
            return BigDecimal.valueOf(comparisonValue * ratio);
        } else if (amount != null) {
            return BigDecimal.valueOf(amount);
        } else {
            throw new IllegalStrategyException("amount and ratio both are null");
        }
    }
}
