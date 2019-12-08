package ru.rosbank.hackathon.bonusSystem.strategy.description;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;

import java.util.List;

import static ru.rosbank.hackathon.bonusSystem.config.JsonConfig.OBJECT_MAPPER;

@Data
public class InstantStrategy {

    private List<AmountInterval> intervals;

    @JsonProperty("mcc_list")
    private List<Integer> mccList;

    @JsonProperty("min_bonus")
    private Double minBonus;

    @JsonProperty("max_bonus")
    private Double maxBonus;

    public static InstantStrategy convertSettingsToStrategy(String settings) {
        try {
            return OBJECT_MAPPER.readValue(settings, InstantStrategy.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
