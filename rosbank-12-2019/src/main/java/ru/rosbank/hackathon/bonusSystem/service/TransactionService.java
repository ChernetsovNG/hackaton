package ru.rosbank.hackathon.bonusSystem.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rosbank.hackathon.bonusSystem.domain.Transaction;
import ru.rosbank.hackathon.bonusSystem.entity.TransactionEntity;
import ru.rosbank.hackathon.bonusSystem.repository.TransactionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final KafkaProducerService kafkaProducerService;

    public TransactionService(TransactionRepository transactionRepository, KafkaProducerService kafkaProducerService) {
        this.transactionRepository = transactionRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Transactional
    public Transaction create(Transaction transaction) {
        TransactionEntity transactionEntity = transaction.toEntity();
        TransactionEntity created = transactionRepository.save(transactionEntity);
        Transaction createdTransaction = created.toDomain();
        kafkaProducerService.sendTransactionEvent(createdTransaction);
        return createdTransaction;
    }

    @Transactional(readOnly = true)
    public List<Transaction> getAll() {
        List<TransactionEntity> transactions = transactionRepository.findAll();
        return transactions.stream()
                .map(TransactionEntity::toDomain)
                .collect(Collectors.toList());
    }
}
