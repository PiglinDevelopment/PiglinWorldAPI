package dev.piglin.piglinworldapi.util;

import java.util.Map;

public record Pair<L, R>(L left, R right) {
    public static <K, V> Pair<K, V> of(final Map.Entry<K, V> pair) {
        final K key;
        final V value;
        if (pair != null) {
            key = pair.getKey();
            value = pair.getValue();
        } else {
            key = null;
            value = null;
        }
        return new Pair<>(key, value);
    }

    public L getKey() {
        return left;
    }

    public R getValue() {
        return right;
    }
}
