package ru.rosbank.hackathon.bonusSystem.strategy.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import org.springframework.stereotype.Component;
import ru.rosbank.hackathon.bonusSystem.dto.Bonus;
import ru.rosbank.hackathon.bonusSystem.dto.Transaction;
import ru.rosbank.hackathon.bonusSystem.entity.ClientEntity;
import ru.rosbank.hackathon.bonusSystem.entity.StrategyEntity;
import ru.rosbank.hackathon.bonusSystem.repository.ClientRepository;
import ru.rosbank.hackathon.bonusSystem.repository.StrategyRepository;
import ru.rosbank.hackathon.bonusSystem.strategy.InstantBonusesCalculateStrategy;
import ru.rosbank.hackathon.bonusSystem.strategy.InstantStrategyType;
import ru.rosbank.hackathon.bonusSystem.strategy.StrategyType;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
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
        List<InstantStrategyType> strategies = getStrategies(transaction);

        // TODO: 07.12.2019 apply strategies to transaction

        return new Bonus();
    }

    private List<InstantStrategyType> getStrategies(Transaction transaction) {
        UUID clientId = transaction.getClientId();
        ClientEntity client = clientRepository.findById(clientId)
                .orElseThrow(EntityNotFoundException::new);
        UUID tariffPlanId = client.getTariffPlanId();

        List<StrategyEntity> strategies = strategyRepository
                .findInstantStrategiesByClient(tariffPlanId, StrategyType.INSTANT.toString());

        return strategies.stream()
                .map(StrategyEntity::getSettings)
                .map(this::convertSettingsToStrategy)
                .collect(Collectors.toList());
    }

    private InstantStrategyType convertSettingsToStrategy(String settings) {
        try {
            return OBJECT_MAPPER.readValue(settings, InstantStrategyType.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
