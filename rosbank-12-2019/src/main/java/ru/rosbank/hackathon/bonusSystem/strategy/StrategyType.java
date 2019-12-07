package ru.rosbank.hackathon.bonusSystem.strategy;

public enum StrategyType {

    /**
     * Стратегия с "мгновенным" вычислением бонусов (как только обрабатываем транзакцию, сразу начисляем бонус)
     */
    INSTANT,

    /**
     * Стратегия с агрегирование данных в заданном временном интервале для вычисления бонусов
     */
    AGGREGATE_DATE
}
