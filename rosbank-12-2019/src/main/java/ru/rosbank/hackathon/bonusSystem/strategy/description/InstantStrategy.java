package ru.rosbank.hackathon.bonusSystem.strategy.description;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;

import java.util.List;

import static ru.rosbank.hackathon.bonusSystem.config.JsonConfig.OBJECT_MAPPER;

@Data
public class InstantStrategy {

    /**
     * Интервалы, в которых определяется количество начисляемых бонусов
     */
    private List<AmountInterval> intervals;

    /**
     * Список MCC, для транзакций в которых применима данная стратегия
     */
    @JsonProperty("mcc_list")
    private List<Integer> mccList;

    /**
     * Минимальное количество начисляемых бонусов
     */
    @JsonProperty("min_bonus")
    private Double minBonus;

    /**
     * Максимальное количество начисляемых бонусов
     */
    @JsonProperty("max_bonus")
    private Double maxBonus;

    public static InstantStrategy convertSettingsToStrategy(String settingsJson) {
        try {
            return OBJECT_MAPPER.readValue(settingsJson, InstantStrategy.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
