package ru.rosbank.hackathon.bonusSystem.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.rosbank.hackathon.bonusSystem.domain.Bonus;
import ru.rosbank.hackathon.bonusSystem.domain.Transaction;
import ru.rosbank.hackathon.bonusSystem.entity.BonusEntity;
import ru.rosbank.hackathon.bonusSystem.repository.BonusRepository;
import ru.rosbank.hackathon.bonusSystem.strategy.impl.InstantStrategy;

@Service
@Slf4j
public class BonusService {

    private final BonusRepository bonusRepository;

    private final InstantStrategy instantStrategy;

    public BonusService(BonusRepository bonusRepository, InstantStrategy instantStrategy) {
        this.bonusRepository = bonusRepository;
        this.instantStrategy = instantStrategy;
    }

    @KafkaListener(topics = "TransactionEvents", clientIdPrefix = "json", containerFactory = "kafkaListenerContainerFactory")
    public void onTransactionEvent(ConsumerRecord<String, Transaction> consumerRecord,
                                   @Payload Transaction transaction) {
        Bonus bonus = instantStrategy.calculate(transaction);
        BonusEntity bonusEntity = bonus.toEntity();
        bonusRepository.save(bonusEntity);
    }
}
