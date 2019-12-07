package ru.rosbank.hackathon.bonusSystem.tuple;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class Pair<K, V> {

    private final K first;

    private final V second;

    private Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }
}
