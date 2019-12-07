package ru.rosbank.hackathon.bonusSystem.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rosbank.hackathon.bonusSystem.domain.Transaction;
import ru.rosbank.hackathon.bonusSystem.service.TransactionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/transactions")
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        log.debug("createTransaction: transaction = {}", transaction);
        Transaction created = transactionService.create(transaction);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Transaction> getTransactions(@RequestParam(name = "clientId", required = false) UUID clientId) {
        return transactionService.getAll(clientId);
    }

    @GetMapping("/{id}")
    public Transaction get(@PathVariable("id") UUID id) {
        return transactionService.get(id);
    }
}
