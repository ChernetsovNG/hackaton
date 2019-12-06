package ru.rosbank.hackathon.bonusSystem.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rosbank.hackathon.bonusSystem.dto.TransactionDto;
import ru.rosbank.hackathon.bonusSystem.service.TransactionService;

import java.util.List;

@RestController
@RequestMapping(path = "/transactions")
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(@RequestBody TransactionDto transaction) {
        log.debug("createTransaction: transaction = {}", transaction);
        TransactionDto created = transactionService.create(transaction);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public List<TransactionDto> getAll() {
        return transactionService.getAll();
    }
}
