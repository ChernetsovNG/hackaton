package ru.rosbank.hackathon.bonusSystem;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.h2.H2ConsoleProperties;
import ru.rosbank.hackathon.bonusSystem.domain.Strategy;
import ru.rosbank.hackathon.bonusSystem.strategy.AmountInterval;
import ru.rosbank.hackathon.bonusSystem.strategy.InstantStrategyType;
import ru.rosbank.hackathon.bonusSystem.strategy.StrategyType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static ru.rosbank.hackathon.bonusSystem.config.JsonConfig.OBJECT_MAPPER;

public class UtilsTest {

    @Test
    public void uuidTest() {
        System.out.println(UUID.randomUUID());
    }

    @Test
    public void instantStrategyTypeTest() throws JsonProcessingException {
        InstantStrategyType instantStrategyType = new InstantStrategyType();
        List<AmountInterval> intervals = new ArrayList<>();
        intervals.add(new AmountInterval(0.0, 500.0, 0.01, null));
        intervals.add(new AmountInterval(500.0, 2000.0, 0.02, null));
        intervals.add(new AmountInterval(2000.0, 5000.0, 0.05, null));
        intervals.add(new AmountInterval(5000.0, null, null, 12.0));
        instantStrategyType.setIntervals(intervals);
        instantStrategyType.setMccList(Arrays.asList(5111, 2738, 3921));
        instantStrategyType.setMinBonus(null);
        instantStrategyType.setMaxBonus(10.0);

        Strategy strategy = new Strategy();
        strategy.setUuid(UUID.randomUUID());
        strategy.setTitle("Test strategy");
        strategy.setType(StrategyType.INSTANT);
        strategy.setSettings(OBJECT_MAPPER.writeValueAsString(instantStrategyType));

        String string = OBJECT_MAPPER.writeValueAsString(strategy);
        System.out.println(string);
    }
}
