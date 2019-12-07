package ru.rosbank.hackathon.bonusSystem.strategy;

public enum StrategyType {

    /**
     * Стратегия с "мгновенным" вычислением бонусов (как только обрабатываем транзакцию, сразу начисляем бонус)
     */
    INSTANT,

    /**
     * Стратегия с агрегирование данных для вычисления бонусов
     */
    AGGREGATE_DATE
}
