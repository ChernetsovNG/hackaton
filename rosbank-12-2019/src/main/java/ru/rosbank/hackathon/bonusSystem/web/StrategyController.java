package ru.rosbank.hackathon.bonusSystem.web;

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

    @GetMapping("/{id}")
    public Strategy get(@PathVariable("id") UUID id) {
        return strategyService.get(id);
    }

    @PostMapping
    public ResponseEntity<Strategy> create(@RequestBody Strategy strategy) {
        Strategy created = strategyService.create(strategy);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping
    public Strategy updateDescription(@RequestBody Strategy strategy) {
        return strategyService.update(strategy);
    }

    @PutMapping("/{id}/delete")
    public ResponseEntity<?> delete(@PathVariable("id") UUID uuid) {
        strategyService.deleteById(uuid);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/link")
    public ResponseEntity<?> linkStrategyToTariffPlan(@RequestParam("strategyId") UUID strategyId,
                                                      @RequestParam("tariffPlanId") UUID tariffPlanId) {
        strategyService.linkToTariffPlan(strategyId, tariffPlanId);
        return ResponseEntity.ok().build();
    }
}
