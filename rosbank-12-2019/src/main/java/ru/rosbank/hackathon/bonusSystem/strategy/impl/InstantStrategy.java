package ru.rosbank.hackathon.bonusSystem.strategy.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import org.springframework.stereotype.Component;
import ru.rosbank.hackathon.bonusSystem.domain.Bonus;
import ru.rosbank.hackathon.bonusSystem.domain.Transaction;
import ru.rosbank.hackathon.bonusSystem.entity.ClientEntity;
import ru.rosbank.hackathon.bonusSystem.entity.StrategyEntity;
import ru.rosbank.hackathon.bonusSystem.repository.ClientRepository;
import ru.rosbank.hackathon.bonusSystem.repository.StrategyRepository;
import ru.rosbank.hackathon.bonusSystem.strategy.InstantBonusesCalculateStrategy;
import ru.rosbank.hackathon.bonusSystem.strategy.InstantStrategyType;
import ru.rosbank.hackathon.bonusSystem.strategy.StrategyType;
import ru.rosbank.hackathon.bonusSystem.tuple.Pair;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.rosbank.hackathon.bonusSystem.config.JsonConfig.OBJECT_MAPPER;

@Data
@Component
public class InstantStrategy implements InstantBonusesCalculateStrategy {

    private final ClientRepository clientRepository;

    private final StrategyRepository strategyRepository;

    public InstantStrategy(ClientRepository clientRepository, StrategyRepository strategyRepository) {
        this.clientRepository = clientRepository;
        this.strategyRepository = strategyRepository;
    }

    @Override
    public Bonus calculate(Transaction transaction) {
        List<Pair<UUID, InstantStrategyType>> strategies = getStrategies(transaction);
        List<Bonus> bonuses = new ArrayList<>();
        for (Pair<UUID, InstantStrategyType> strategy : strategies) {
            UUID strategyId = strategy.getFirst();
            InstantStrategyType instantStrategyType = strategy.getSecond();
            bonuses.add(instantStrategyType.calculateBonus(transaction, strategyId));
        }
        bonuses = bonuses.stream().filter(Objects::nonNull).collect(Collectors.toList());
        // TODO: 07.12.2019 пока что берём максимальный из вычисленных бонусов
        return bonuses.isEmpty() ? null :
                bonuses.stream()
                        .max(Comparator.comparing(Bonus::getAmount))
                        .orElseThrow(IllegalStateException::new);
    }

    private List<Pair<UUID, InstantStrategyType>> getStrategies(Transaction transaction) {
        UUID clientId = transaction.getClientId();
        ClientEntity client = clientRepository.findById(clientId)
                .orElseThrow(EntityNotFoundException::new);
        UUID tariffPlanId = client.getTariffPlanId();

        List<StrategyEntity> strategies = strategyRepository.findInstantStrategiesByClient(
                tariffPlanId, StrategyType.INSTANT.toString());

        List<Pair<UUID, InstantStrategyType>> result = new ArrayList<>();
        for (StrategyEntity strategy : strategies) {
            UUID strategyId = strategy.getUuid();
            String settings = strategy.getSettings();
            InstantStrategyType instantStrategyType = convertSettingsToStrategy(settings);
            result.add(Pair.of(strategyId, instantStrategyType));
        }
        return result;
    }

    private InstantStrategyType convertSettingsToStrategy(String settings) {
        try {
            return OBJECT_MAPPER.readValue(settings, InstantStrategyType.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
