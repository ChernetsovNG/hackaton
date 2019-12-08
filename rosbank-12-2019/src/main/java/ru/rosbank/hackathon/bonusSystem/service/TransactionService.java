package ru.rosbank.hackathon.bonusSystem.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rosbank.hackathon.bonusSystem.domain.Transaction;
import ru.rosbank.hackathon.bonusSystem.entity.TransactionEntity;
import ru.rosbank.hackathon.bonusSystem.repository.TransactionRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
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

    @Transactional
    public List<Transaction> createAll(List<Transaction> transactions) {
        List<TransactionEntity> transactionEntities = transactions.stream()
                .map(Transaction::toEntity)
                .collect(Collectors.toList());
        List<TransactionEntity> created = transactionRepository.saveAll(transactionEntities);
        List<Transaction> createdTransactions = created.stream()
                .map(TransactionEntity::toDomain)
                .collect(Collectors.toList());
        createdTransactions.forEach(kafkaProducerService::sendTransactionEvent);
        return createdTransactions;
    }

    @Transactional(readOnly = true)
    public List<Transaction> getAll(UUID clientId) {
        List<TransactionEntity> transactions = clientId != null ?
                transactionRepository.findAllByClientId(clientId) :
                transactionRepository.findAll();
        return transactions.stream()
                .map(TransactionEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Transaction get(UUID id) {
        return transactionRepository.findById(id)
                .map(TransactionEntity::toDomain)
                .orElseThrow(EntityNotFoundException::new);
    }
}
