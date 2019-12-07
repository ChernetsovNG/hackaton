package ru.rosbank.hackathon.bonusSystem.strategy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class AggregateTimeSettings {

    @JsonProperty("from_time")
    private OffsetDateTime fromTime;

    @JsonProperty("to_time")
    private OffsetDateTime toTime;

    @JsonProperty("time_unit")
    private AggregateTimeUnit timeUnit;

    @JsonProperty("quantity")
    private Integer quantity;
}
