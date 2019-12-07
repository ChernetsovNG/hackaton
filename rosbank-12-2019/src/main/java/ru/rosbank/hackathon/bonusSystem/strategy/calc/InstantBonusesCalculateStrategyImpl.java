package ru.rosbank.hackathon.bonusSystem.strategy.calc;

import lombok.Data;
import org.springframework.stereotype.Service;
import ru.rosbank.hackathon.bonusSystem.domain.Bonus;
import ru.rosbank.hackathon.bonusSystem.domain.Transaction;
import ru.rosbank.hackathon.bonusSystem.entity.ClientEntity;
import ru.rosbank.hackathon.bonusSystem.entity.StrategyEntity;
import ru.rosbank.hackathon.bonusSystem.exception.IllegalStrategyException;
import ru.rosbank.hackathon.bonusSystem.repository.ClientRepository;
import ru.rosbank.hackathon.bonusSystem.repository.StrategyRepository;
import ru.rosbank.hackathon.bonusSystem.strategy.description.AmountInterval;
import ru.rosbank.hackathon.bonusSystem.strategy.description.InstantStrategy;
import ru.rosbank.hackathon.bonusSystem.strategy.enums.StrategyType;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.rosbank.hackathon.bonusSystem.strategy.description.InstantStrategy.convertSettingsToStrategy;

@Data
@Service
public class InstantBonusesCalculateStrategyImpl implements InstantBonusesCalculateStrategy {

    private final ClientRepository clientRepository;

    private final StrategyRepository strategyRepository;

    public InstantBonusesCalculateStrategyImpl(ClientRepository clientRepository, StrategyRepository strategyRepository) {
        this.clientRepository = clientRepository;
        this.strategyRepository = strategyRepository;
    }

    @Override
    public Bonus calculate(Transaction transaction) {
        Map<UUID, InstantStrategy> strategies = getStrategies(transaction);
        List<Bonus> bonuses = strategies.entrySet().stream()
                .map(entry -> {
                    UUID strategyId = entry.getKey();
                    InstantStrategy strategy = entry.getValue();
                    return calculateBonus(transaction, strategyId, strategy);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        // TODO: 07.12.2019 пока что берём максимальный из вычисленных по разным стратегиям бонусов
        return bonuses.isEmpty() ? null :
                bonuses.stream()
                        .max(Comparator.comparing(Bonus::getAmount))
                        .orElseThrow(IllegalStateException::new);
    }

    private Map<UUID, InstantStrategy> getStrategies(Transaction transaction) {
        // 1. Находим клиента по транзакции
        UUID clientId = transaction.getClientId();
        ClientEntity client = clientRepository.findById(clientId)
                .orElseThrow(EntityNotFoundException::new);

        // 2. Находим "мгновенные" стратегии для клиента
        List<StrategyEntity> strategies = strategyRepository.findInstantStrategiesByTariffPlan(
                client.getTariffPlanId(), StrategyType.INSTANT.toString());

        Map<UUID, InstantStrategy> result = new HashMap<>();
        for (StrategyEntity strategy : strategies) {
            UUID strategyId = strategy.getUuid();
            String settings = strategy.getSettings();
            InstantStrategy instantStrategy = convertSettingsToStrategy(settings);
            result.put(strategyId, instantStrategy);
        }
        return result;
    }

    private Bonus calculateBonus(Transaction transaction, UUID strategyId, InstantStrategy strategy) {
        List<AmountInterval> intervals = strategy.getIntervals();
        List<Integer> mccList = strategy.getMccList();
        Double minBonus = strategy.getMinBonus();
        Double maxBonus = strategy.getMaxBonus();
        Bonus bonus;
        if (mccList == null) {  // применяем ко всем MCC
            bonus = calculateBonusByIntervals(transaction, strategyId, intervals);
        } else {  // проверяем, что для нашей транзакции MCC в списке
            Integer transactionMcc = transaction.getMcc();
            if (!mccList.contains(transactionMcc)) {
                return null;
            }
            bonus = calculateBonusByIntervals(transaction, strategyId, intervals);
        }
        // проверяем ограничения на мин. и макс. значение
        if (bonus != null) {
            bonus.checkThresholdValues(minBonus, maxBonus);
        }
        return bonus;
    }

    private Bonus calculateBonusByIntervals(Transaction transaction, UUID strategyId, List<AmountInterval> intervals) {
        for (AmountInterval interval : intervals) {
            if (interval.valueInInterval(transaction.getAmount().doubleValue())) {
                return calculateBonusByInterval(transaction, interval, strategyId);
            }
        }
        return null;
    }

    private Bonus calculateBonusByInterval(Transaction transaction, AmountInterval interval, UUID strategyId) {
        Bonus bonus = new Bonus();
        bonus.setUuid(UUID.randomUUID());
        bonus.setTransactions(Collections.singletonList(transaction));
        bonus.setClientId(transaction.getClientId());
        bonus.setAmount(calculateBonusAmount(transaction.getAmount(), interval.getRatio(), interval.getAmount()));
        bonus.setCreateTime(OffsetDateTime.now());
        bonus.setUpdateTime(OffsetDateTime.now());
        bonus.setStrategyId(strategyId);
        return bonus;
    }

    private BigDecimal calculateBonusAmount(BigDecimal transactionAmount, Double ratio, Double amount) {
        if (ratio != null) {
            return transactionAmount.multiply(BigDecimal.valueOf(ratio));
        } else if (amount != null) {
            return BigDecimal.valueOf(amount);
        } else {
            throw new IllegalStrategyException("amount and ratio both are null");
        }
    }
}
