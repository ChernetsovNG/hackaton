package ru.rosbank.hackathon.bonusSystem.strategy.description;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import ru.rosbank.hackathon.bonusSystem.exception.IllegalStrategyException;
import ru.rosbank.hackathon.bonusSystem.strategy.enums.AggregateFunction;

import java.util.List;

import static ru.rosbank.hackathon.bonusSystem.config.JsonConfig.OBJECT_MAPPER;

@Data
public class AggregateStrategy {

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

    @JsonProperty("bonus_max_age_ms")
    private Long bonusMaxAgeMs;

    public static AggregateStrategy convertSettingsToStrategy(String settings) {
        try {
            return OBJECT_MAPPER.readValue(settings, AggregateStrategy.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStrategyException(e);
        }
    }
}
