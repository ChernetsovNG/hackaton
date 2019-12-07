package ru.rosbank.hackathon.bonusSystem.strategy.enums;

public enum StrategyType {

    /**
     * Стратегии с "мгновенным" вычислением бонусов (как только обрабатываем транзакцию, сразу начисляем бонус)
     */
    INSTANT,

    /**
     * Стратегии с агрегирование данных в заданном временном интервале для вычисления бонусов
     */
    AGGREGATE_DATE
}
