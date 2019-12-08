package ru.rosbank.hackathon.bonusSystem.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rosbank.hackathon.bonusSystem.domain.Bonus;
import ru.rosbank.hackathon.bonusSystem.domain.Strategy;
import ru.rosbank.hackathon.bonusSystem.domain.Transaction;
import ru.rosbank.hackathon.bonusSystem.entity.BonusEntity;
import ru.rosbank.hackathon.bonusSystem.entity.StrategyEntity;
import ru.rosbank.hackathon.bonusSystem.repository.BonusRepository;
import ru.rosbank.hackathon.bonusSystem.repository.StrategyRepository;
import ru.rosbank.hackathon.bonusSystem.strategy.calc.InstantBonusesCalculateStrategyImpl;

import javax.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BonusService {

    private final BonusRepository bonusRepository;

    private final StrategyRepository strategyRepository;

    private final InstantBonusesCalculateStrategyImpl instantBonusesCalculateStrategyImpl;

    public BonusService(BonusRepository bonusRepository, StrategyRepository strategyRepository,
                        InstantBonusesCalculateStrategyImpl instantBonusesCalculateStrategyImpl) {
        this.bonusRepository = bonusRepository;
        this.strategyRepository = strategyRepository;
        this.instantBonusesCalculateStrategyImpl = instantBonusesCalculateStrategyImpl;
    }

    @KafkaListener(topics = "TransactionEvents", clientIdPrefix = "json", containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void onTransactionEvent(ConsumerRecord<String, Transaction> consumerRecord, @Payload Transaction transaction) {
        Bonus bonus = instantBonusesCalculateStrategyImpl.calculate(transaction);
        if (bonus != null) {
            BonusEntity bonusEntity = bonus.toEntity();
            bonusRepository.save(bonusEntity);
        }
    }

    @Transactional(readOnly = true)
    public List<Bonus> getClientBonuses(UUID clientId, OffsetDateTime fromDate, OffsetDateTime toDate) {
        List<BonusEntity> bonusEntities;
        if (fromDate == null && toDate == null) {
            bonusEntities = bonusRepository.findAllByClientId(clientId);
        } else if (fromDate != null && toDate == null) {
            bonusEntities = bonusRepository.findAllByClientIdAndCreateTimeGreaterThanEqual(clientId, fromDate);
        } else if (fromDate == null) {
            bonusEntities = bonusRepository.findAllByClientIdAndCreateTimeLessThanEqual(clientId, toDate);
        } else {
            bonusEntities = bonusRepository.findAllByClientIdAndCreateTimeGreaterThanEqualAndCreateTimeLessThanEqual(
                    clientId, fromDate, toDate);
        }
        List<Bonus> bonuses = bonusEntities.stream()
                .map(BonusEntity::toDomain)
                .collect(Collectors.toList());
        fillByStrategies(bonuses);
        return bonuses;
    }

    @Transactional(readOnly = true)
    public Bonus getBonus(UUID id) {
        return bonusRepository.findById(id)
                .map(BonusEntity::toDomain)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    @Scheduled(fixedRate = 60_000L)
    public void clearOldBonuses() {
        List<BonusEntity> bonuses = bonusRepository.findAllByTimeToLiveNotNull();
        if (bonuses.isEmpty()) {
            return;
        }
        OffsetDateTime now = OffsetDateTime.now();
        List<BonusEntity> oldBonuses = bonuses.stream()
                .filter(bonusEntity -> bonusEntity.getTimeToLive().isBefore(now))
                .collect(Collectors.toList());
        if (!oldBonuses.isEmpty()) {
            bonusRepository.deleteAll(oldBonuses);
        }
    }

    private void fillByStrategies(List<Bonus> bonuses) {
        if (bonuses.isEmpty()) {
            return;
        }
        Set<UUID> strategyIds = bonuses.stream()
                .map(Bonus::getStrategyId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<UUID, Strategy> strategiesMap = strategyRepository.findAllById(strategyIds).stream()
                .map(StrategyEntity::toDomain)
                .collect(Collectors.toMap(Strategy::getUuid, Function.identity()));
        for (Bonus bonus : bonuses) {
            UUID strategyId = bonus.getStrategyId();
            if (strategyId != null) {
                Strategy strategy = strategiesMap.get(strategyId);
                bonus.setStrategy(strategy);
            }
        }
    }
}
