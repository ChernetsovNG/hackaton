package ru.rosbank.hackathon.bonusSystem.strategy;

import ru.rosbank.hackathon.bonusSystem.dto.Bonus;
import ru.rosbank.hackathon.bonusSystem.dto.Transaction;

public interface InstantBonusesCalculateStrategy extends BonusesCalculateStrategy {

    Bonus calculate(Transaction transaction);
}
