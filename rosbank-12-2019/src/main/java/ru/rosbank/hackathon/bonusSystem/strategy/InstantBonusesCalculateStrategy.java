package ru.rosbank.hackathon.bonusSystem.strategy;

import ru.rosbank.hackathon.bonusSystem.domain.Bonus;
import ru.rosbank.hackathon.bonusSystem.domain.Transaction;

public interface InstantBonusesCalculateStrategy extends BonusesCalculateStrategy {

    Bonus calculate(Transaction transaction);
}
