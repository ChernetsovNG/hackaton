package ru.rosbank.hackathon.bonusSystem.strategy.description;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.rosbank.hackathon.bonusSystem.strategy.enums.AggregateTimeUnit;

import java.time.OffsetDateTime;

@Data
public class AggregateTimeSettings {

    /**
     * Начальное время интервала планирования
     */
    @JsonProperty("from_time")
    private OffsetDateTime fromTime;

    /**
     * Конечное время интервала планирования
     */
    @JsonProperty("to_time")
    private OffsetDateTime toTime;

    /**
     * Временная единица
     */
    @JsonProperty("time_unit")
    private AggregateTimeUnit timeUnit;

    /**
     * Количество временных единиц, после прошествия которых нужно повторть запланированную задачу
     */
    @JsonProperty("quantity")
    private Integer quantity;
}
