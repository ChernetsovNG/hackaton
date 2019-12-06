package ru.rosbank.hackathon.bonusSystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rosbank.hackathon.bonusSystem.dto.Transaction;
import ru.rosbank.hackathon.bonusSystem.entity.TransactionEntity;
import ru.rosbank.hackathon.bonusSystem.repository.TransactionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction create(Transaction transaction) {
        TransactionEntity transactionEntity = transaction.toEntity();
        TransactionEntity created = transactionRepository.save(transactionEntity);
        return created.toDto();
    }

    @Transactional(readOnly = true)
    public List<Transaction> getAll() {
        List<TransactionEntity> transactions = transactionRepository.findAll();
        return transactions.stream()
                .map(TransactionEntity::toDto)
                .collect(Collectors.toList());
    }
}
