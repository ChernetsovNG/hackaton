package ru.rosbank.hackathon.bonusSystem.exception;

import com.fasterxml.jackson.core.JsonProcessingException;

public class IllegalStrategyException extends RuntimeException {

    public IllegalStrategyException(String message) {
        super(message);
    }

    public IllegalStrategyException(JsonProcessingException e) {
        super(e);
    }
}
