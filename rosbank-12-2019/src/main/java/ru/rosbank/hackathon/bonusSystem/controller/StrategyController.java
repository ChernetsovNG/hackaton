package ru.rosbank.hackathon.bonusSystem.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rosbank.hackathon.bonusSystem.domain.Strategy;
import ru.rosbank.hackathon.bonusSystem.service.StrategyService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/strategies")
@Slf4j
public class StrategyController {

    private final StrategyService strategyService;

    public StrategyController(StrategyService strategyService) {
        this.strategyService = strategyService;
    }

    @GetMapping
    public List<Strategy> getAllStrategies() {
        return strategyService.getAll();
    }

    @PostMapping
    public ResponseEntity<Strategy> create(@RequestBody Strategy strategy) {
        Strategy created = strategyService.create(strategy);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/link")
    public ResponseEntity<?> linkStrategyToTariffPlan(@RequestParam("strategyId") UUID strategyId,
                                                      @RequestParam("tariffPlanId") UUID tariffPlanId) {
        strategyService.linkToTariffPlan(strategyId, tariffPlanId);
        return ResponseEntity.ok().build();
    }
}
