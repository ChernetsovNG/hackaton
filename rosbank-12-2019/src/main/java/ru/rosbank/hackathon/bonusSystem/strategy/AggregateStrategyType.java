package ru.rosbank.hackathon.bonusSystem.strategy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AggregateStrategyType {

    @JsonProperty("aggregate_time_settings")
    private AggregateTimeSettings timeSettings;

    @JsonProperty("aggregate_function")
    private AggregateFunction aggregateFunction;

    private List<AmountInterval> intervals;

    @JsonProperty("mcc_list")
    private List<Integer> mccList;

    @JsonProperty("min_bonus")
    private Double minBonus;

    @JsonProperty("max_bonus")
    private Double maxBonus;
}
