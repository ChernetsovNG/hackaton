package ru.rosbank.hackathon.bonusSystem.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rosbank.hackathon.bonusSystem.domain.Strategy;
import ru.rosbank.hackathon.bonusSystem.entity.StrategyEntity;
import ru.rosbank.hackathon.bonusSystem.repository.StrategyRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StrategyService {

    private final StrategyRepository strategyRepository;

    public StrategyService(StrategyRepository strategyRepository) {
        this.strategyRepository = strategyRepository;
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
        return created.toDomain();
    }

    @Transactional
    public void linkToTariffPlan(UUID strategyId, UUID tariffPlanId) {
        strategyRepository.linkStrategyToTariffPlan(strategyId, tariffPlanId);
    }
}
