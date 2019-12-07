package ru.rosbank.hackathon.bonusSystem.strategy;

import lombok.Getter;

public enum AggregateTimeUnit {
    MINUTES(1),
    HOURS(60),
    DAYS(24 * 60),
    WEEKS(24 * 60 * 7);

    @Getter
    private final int minutes;

    AggregateTimeUnit(int minutes) {
        this.minutes = minutes;
    }
}
