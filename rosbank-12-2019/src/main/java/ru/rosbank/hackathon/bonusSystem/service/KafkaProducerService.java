package ru.rosbank.hackathon.bonusSystem.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.rosbank.hackathon.bonusSystem.domain.Transaction;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> template;
    private final String topicName;

    public KafkaProducerService(KafkaTemplate<String, Object> template,
                                @Value("${transactions.topic-name}") String topicName) {
        this.template = template;
        this.topicName = topicName;
    }

    public void sendTransactionEvent(Transaction transaction) {
        template.send(topicName, transaction);
    }
}
