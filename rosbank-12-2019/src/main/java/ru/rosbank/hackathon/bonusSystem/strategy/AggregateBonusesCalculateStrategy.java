package ru.rosbank.hackathon.bonusSystem.strategy;

import ru.rosbank.hackathon.bonusSystem.domain.Bonus;

import java.time.ZonedDateTime;
import java.util.UUID;

public interface AggregateBonusesCalculateStrategy extends BonusesCalculateStrategy {

    Bonus calculate(UUID clientId, ZonedDateTime from, ZonedDateTime to);
}
