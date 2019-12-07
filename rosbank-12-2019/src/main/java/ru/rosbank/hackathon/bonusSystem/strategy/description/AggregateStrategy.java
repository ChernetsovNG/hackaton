package ru.rosbank.hackathon.bonusSystem.strategy.description;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import ru.rosbank.hackathon.bonusSystem.strategy.enums.AggregateFunction;

import java.util.List;

import static ru.rosbank.hackathon.bonusSystem.config.JsonConfig.OBJECT_MAPPER;

@Data
public class AggregateStrategy {

    /**
     * Временные настройки агрегирующей стратегии (вида, "каждые 5 дней", "каждые 3 недели", и т.д.)
     */
    @JsonProperty("aggregate_time_settings")
    private AggregateTimeSettings timeSettings;

    /**
     * Тип агрегатной функции, применяемой к транзакциями во временном интервале
     */
    @JsonProperty("aggregate_function")
    private AggregateFunction aggregateFunction;

    private List<AmountInterval> intervals;

    @JsonProperty("mcc_list")
    private List<Integer> mccList;

    @JsonProperty("min_bonus")
    private Double minBonus;

    @JsonProperty("max_bonus")
    private Double maxBonus;

    public static AggregateStrategy convertSettingsToStrategy(String settingsJson) {
        try {
            return OBJECT_MAPPER.readValue(settingsJson, AggregateStrategy.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
