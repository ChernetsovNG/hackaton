package ru.rosbank.hackathon.bonusSystem.strategy;

import lombok.Getter;

public enum AggregateTimeUnit {
    MINUTES(1),
    HOURS(60),
    DAYS(1440),
    WEEKS(10080);

    @Getter
    private final int minutes;

    AggregateTimeUnit(int minutes) {
        this.minutes = minutes;
    }
}
