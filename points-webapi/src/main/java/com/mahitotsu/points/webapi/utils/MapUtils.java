package com.mahitotsu.points.webapi.utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {

    public static class MapBuilder<K, V> {

        private MapBuilder(final Map<K, V> map) {
            this.resultMap = map;
        }

        private Map<K, V> resultMap;

        public MapBuilder<K, V> put(final K key, final V value) {
            this.resultMap.put(key, value);
            return this;
        };

        public Map<K, V> build() {
            return this.resultMap;
        }
    }

    public static <K, V> MapBuilder<K, V> builder() {
        return new MapBuilder<>(new HashMap<>());
    }

    public static <K, V> MapBuilder<K, V> builder(final Map<K, V> map) {
        return new MapBuilder<>(map);
    }

    private MapUtils() {
    }
}
