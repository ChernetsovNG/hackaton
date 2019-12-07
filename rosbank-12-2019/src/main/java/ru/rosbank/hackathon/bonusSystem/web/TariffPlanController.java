package ru.rosbank.hackathon.bonusSystem.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rosbank.hackathon.bonusSystem.domain.TariffPlan;
import ru.rosbank.hackathon.bonusSystem.service.TariffPlanService;

import java.util.List;

@RestController
@RequestMapping(path = "/tariffPlans")
@Slf4j
public class TariffPlanController {

    private final TariffPlanService tariffPlanService;

    public TariffPlanController(TariffPlanService tariffPlanService) {
        this.tariffPlanService = tariffPlanService;
    }

    @GetMapping
    public List<TariffPlan> getAll() {
        return tariffPlanService.getAll();
    }

    @PostMapping
    public ResponseEntity<TariffPlan> create(@RequestBody TariffPlan tariffPlan) {
        TariffPlan created = tariffPlanService.create(tariffPlan);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
