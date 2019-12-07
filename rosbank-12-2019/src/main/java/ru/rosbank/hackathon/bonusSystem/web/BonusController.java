package ru.rosbank.hackathon.bonusSystem.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.rosbank.hackathon.bonusSystem.domain.Bonus;
import ru.rosbank.hackathon.bonusSystem.service.BonusService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/bonuses")
@Slf4j
public class BonusController {

    private final BonusService bonusService;

    public BonusController(BonusService bonusService) {
        this.bonusService = bonusService;
    }

    @GetMapping
    public List<Bonus> getClientBonuses(@RequestParam("clientId") UUID clientId,
                                        @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fromDate,
                                        @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime toDate) {
        return bonusService.getClientBonuses(clientId, fromDate, toDate);
    }

    @GetMapping("/{id}")
    public Bonus getBonus(@PathVariable("id") UUID id) {
        return bonusService.getBonus(id);
    }
}
