package ru.rosbank.hackathon.bonusSystem.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rosbank.hackathon.bonusSystem.domain.Bonus;
import ru.rosbank.hackathon.bonusSystem.domain.Transaction;
import ru.rosbank.hackathon.bonusSystem.entity.BonusEntity;
import ru.rosbank.hackathon.bonusSystem.entity.TransactionEntity;
import ru.rosbank.hackathon.bonusSystem.exception.IllegalStrategyException;
import ru.rosbank.hackathon.bonusSystem.repository.BonusRepository;
import ru.rosbank.hackathon.bonusSystem.repository.TransactionRepository;
import ru.rosbank.hackathon.bonusSystem.strategy.AggregateStrategyType;
import ru.rosbank.hackathon.bonusSystem.strategy.AggregateTimeSettings;
import ru.rosbank.hackathon.bonusSystem.strategy.AmountInterval;
import ru.rosbank.hackathon.bonusSystem.tuple.Pair;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AggregateStrategyService {

    private final TransactionRepository transactionRepository;

    private final BonusRepository bonusRepository;

    public AggregateStrategyService(TransactionRepository transactionRepository, BonusRepository bonusRepository) {
        this.transactionRepository = transactionRepository;
        this.bonusRepository = bonusRepository;
    }

    public void performAggregateStrategies(List<AggregateStrategyType> aggregateStrategies, List<UUID> strategyIds,
                                           List<OffsetDateTime> nextTimeList) {
        int count = aggregateStrategies.size();
        for (int i = 0; i < count; i++) {
            AggregateStrategyType strategy = aggregateStrategies.get(i);
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
    public void performAggregateStrategy(AggregateStrategyType strategy, UUID strategyId,
                                         OffsetDateTime fromTime, OffsetDateTime toTime) {
        // выбираем транзакции в заданном временном диапазоне
        List<TransactionEntity> transactions = transactionRepository
                .findAllByTimeGreaterThanEqualAndTimeLessThanEqual(fromTime, toTime);
        // группируем их по пользователю
        Map<UUID, List<TransactionEntity>> transactionByUsersMap = transactions.stream()
                .collect(Collectors.groupingBy(TransactionEntity::getClientId));
        // для каждого пользователя считаем сумму по его тразакциям
        Map<UUID, List<Transaction>> domainTransactionByUsersMap = transactionByUsersMap.entrySet().stream()
                .map(entry -> {
                    UUID clientId = entry.getKey();
                    List<TransactionEntity> clientTransactions = entry.getValue();
                    List<Transaction> domainTransactions = clientTransactions.stream()
                            .map(TransactionEntity::toDomain)
                            .collect(Collectors.toList());
                    return Pair.of(clientId, domainTransactions);
                })
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
        domainTransactionByUsersMap.forEach((clientId, domainTransactions) -> {
            Bonus bonus = calculateBonus(clientId, domainTransactions, strategy, strategyId);
            // сохраняем вычисленный бонус в БД
            BonusEntity bonusEntity = bonus.toEntity();
            bonusRepository.save(bonusEntity);
        });
    }

    private Bonus calculateBonus(UUID clientId, List<Transaction> transactions,
                                 AggregateStrategyType strategy, UUID strategyId) {
        List<Integer> mccList = strategy.getMccList();
        List<AmountInterval> intervals = strategy.getIntervals();
        Double minBonus = strategy.getMinBonus();
        Double maxBonus = strategy.getMaxBonus();
        Bonus bonus;
        if (mccList == null) {  // применяем ко всем MCC
            bonus = calculateBonusByIntervals(transactions, intervals, clientId, strategyId);
        } else {  // фильтруем список транзакций по применимым к ним MCC
            transactions = transactions.stream()
                    .filter(transaction -> mccList.contains(transaction.getMcc()))
                    .collect(Collectors.toList());
            bonus = calculateBonusByIntervals(transactions, intervals, clientId, strategyId);
        }
        // проверяем ограничения на мин. и макс. значение
        checkThresholdValues(bonus, minBonus, maxBonus);
        return bonus;
    }

    private Bonus calculateBonusByIntervals(List<Transaction> transactions, List<AmountInterval> intervals,
                                            UUID clientId, UUID strategyId) {
        double transactionsSum = transactions.stream()
                .map(Transaction::getAmount)
                .map(BigDecimal::doubleValue)
                .mapToDouble(v -> v)
                .sum();
        for (AmountInterval interval : intervals) {
            if (transactionsSumInInterval(transactionsSum, interval)) {
                return calculateBonusByInterval(transactions, transactionsSum, interval, clientId, strategyId);
            }
        }
        return null;
    }

    private Bonus calculateBonusByInterval(List<Transaction> transactions, Double transactionsSum,
                                           AmountInterval interval, UUID clientId, UUID strategyId) {
        Double ratio = interval.getRatio();
        Double amount = interval.getAmount();
        BigDecimal bonusAmount = calculateBonusAmount(transactionsSum, ratio, amount);

        Bonus bonus = new Bonus();
        bonus.setUuid(UUID.randomUUID());
        bonus.setTransactions(transactions);
        bonus.setClientId(clientId);
        bonus.setAmount(bonusAmount);
        bonus.setCreateTime(OffsetDateTime.now());
        bonus.setUpdateTime(OffsetDateTime.now());
        bonus.setStrategyId(strategyId);

        return bonus;
    }

    private BigDecimal calculateBonusAmount(Double transactionsSum, Double ratio, Double amount) {
        if (ratio != null) {
            return BigDecimal.valueOf(transactionsSum * ratio);
        } else if (amount != null) {
            return BigDecimal.valueOf(amount);
        } else {
            throw new IllegalStrategyException("amount and ratio both are null");
        }
    }

    private void checkThresholdValues(Bonus bonus, Double minBonus, Double maxBonus) {
        if (bonus == null) {
            return;
        }
        if (minBonus == null && maxBonus == null) {
            return;
        } else if (minBonus != null && maxBonus == null) {
            if (bonus.getAmount().compareTo(BigDecimal.valueOf(minBonus)) < 0) {
                bonus.setAmount(BigDecimal.valueOf(minBonus));
            }
        } else if (minBonus == null) {
            if (bonus.getAmount().compareTo(BigDecimal.valueOf(maxBonus)) > 0) {
                bonus.setAmount(BigDecimal.valueOf(maxBonus));
            }
        } else {
            if (bonus.getAmount().compareTo(BigDecimal.valueOf(minBonus)) < 0) {
                bonus.setAmount(BigDecimal.valueOf(minBonus));
            }
            if (bonus.getAmount().compareTo(BigDecimal.valueOf(maxBonus)) > 0) {
                bonus.setAmount(BigDecimal.valueOf(maxBonus));
            }
        }
    }

    private boolean transactionsSumInInterval(Double transactionsSum, AmountInterval interval) {
        Double from = interval.getFrom();
        Double to = interval.getTo();
        return to != null ? transactionsSum >= from && transactionsSum <= to : transactionsSum >= from;
    }
}
