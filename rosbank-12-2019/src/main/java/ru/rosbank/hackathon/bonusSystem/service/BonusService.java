package ru.rosbank.hackathon.bonusSystem.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rosbank.hackathon.bonusSystem.domain.Bonus;
import ru.rosbank.hackathon.bonusSystem.domain.Transaction;
import ru.rosbank.hackathon.bonusSystem.entity.BonusEntity;
import ru.rosbank.hackathon.bonusSystem.repository.BonusRepository;
import ru.rosbank.hackathon.bonusSystem.strategy.calc.InstantBonusesCalculateStrategyImpl;

import javax.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BonusService {

    private final BonusRepository bonusRepository;

    private final InstantBonusesCalculateStrategyImpl instantBonusesCalculateStrategyImpl;

    public BonusService(BonusRepository bonusRepository, InstantBonusesCalculateStrategyImpl instantBonusesCalculateStrategyImpl) {
        this.bonusRepository = bonusRepository;
        this.instantBonusesCalculateStrategyImpl = instantBonusesCalculateStrategyImpl;
    }

    @KafkaListener(topics = "TransactionEvents", clientIdPrefix = "json", containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void onTransactionEvent(ConsumerRecord<String, Transaction> consumerRecord, @Payload Transaction transaction) {
        Bonus bonus = instantBonusesCalculateStrategyImpl.calculate(transaction);
        BonusEntity bonusEntity = bonus.toEntity();
        bonusRepository.save(bonusEntity);
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
        return bonusEntities.stream()
                .map(BonusEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Bonus getBonus(UUID id) {
        return bonusRepository.findById(id)
                .map(BonusEntity::toDomain)
                .orElseThrow(EntityNotFoundException::new);
    }
}
