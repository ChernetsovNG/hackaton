package ru.rosbank.hackathon.bonusSystem;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import ru.rosbank.hackathon.bonusSystem.domain.Strategy;
import ru.rosbank.hackathon.bonusSystem.strategy.description.AggregateStrategy;
import ru.rosbank.hackathon.bonusSystem.strategy.description.AggregateTimeSettings;
import ru.rosbank.hackathon.bonusSystem.strategy.description.AmountInterval;
import ru.rosbank.hackathon.bonusSystem.strategy.description.InstantStrategy;
import ru.rosbank.hackathon.bonusSystem.strategy.enums.AggregateTimeUnit;
import ru.rosbank.hackathon.bonusSystem.strategy.enums.StrategyType;

import java.time.OffsetDateTime;
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
        /*
        Здесь для примера: 1% на все покупки, 5% на 3 категории. Категории определяются по мсс
         */

        InstantStrategy instantStrategy = new InstantStrategy();
        List<AmountInterval> intervals = new ArrayList<>();
        intervals.add(new AmountInterval(0.0, null, 0.05, null));
        instantStrategy.setIntervals(intervals);
        instantStrategy.setMccList(Arrays.asList(1234, 5678, 6543));
        instantStrategy.setMinBonus(null);
        instantStrategy.setMaxBonus(null);

        Strategy strategy = new Strategy();
        strategy.setUuid(UUID.randomUUID());
        strategy.setTitle("Test strategy");
        strategy.setType(StrategyType.INSTANT);
        strategy.setSettings(OBJECT_MAPPER.writeValueAsString(instantStrategy));

        String string = OBJECT_MAPPER.writeValueAsString(strategy);
        System.out.println(string);
    }

    @Test
    public void aggregatedStrategyTypeTest() throws JsonProcessingException {
        AggregateStrategy aggregateStrategy = new AggregateStrategy();
        List<AmountInterval> intervals = new ArrayList<>();
        intervals.add(new AmountInterval(0.0, 500.0, 0.01, null));
        intervals.add(new AmountInterval(500.0, 2000.0, 0.02, null));
        intervals.add(new AmountInterval(2000.0, 5000.0, 0.05, null));
        intervals.add(new AmountInterval(5000.0, null, null, 12.0));
        aggregateStrategy.setIntervals(intervals);
        aggregateStrategy.setMccList(Arrays.asList(5111, 2738, 3921));
        aggregateStrategy.setMinBonus(null);
        aggregateStrategy.setMaxBonus(10.0);

        AggregateTimeSettings timeSettings = new AggregateTimeSettings();
        timeSettings.setFromTime(OffsetDateTime.now().plusMinutes(5));
        timeSettings.setQuantity(5);
        timeSettings.setTimeUnit(AggregateTimeUnit.MINUTES);
        aggregateStrategy.setTimeSettings(timeSettings);

        Strategy strategy = new Strategy();
        strategy.setUuid(UUID.randomUUID());
        strategy.setTitle("Test aggregate strategy");
        strategy.setType(StrategyType.AGGREGATE_DATE);
        strategy.setSettings(OBJECT_MAPPER.writeValueAsString(aggregateStrategy));

        String string = OBJECT_MAPPER.writeValueAsString(strategy);
        System.out.println(string);
    }
}
