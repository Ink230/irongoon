package lod.irongoon.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Immutable, fully parsed configuration boundary shared by file and campaign sources. */
public record IrongoonConfigSnapshot(String source, Map<String, Object> values, List<String> warnings) {
    public IrongoonConfigSnapshot {
        final Map<String, Object> copiedValues = new LinkedHashMap<>();
        for(final var entry : values.entrySet()) {
            copiedValues.put(entry.getKey(), copyValue(entry.getValue()));
        }
        values = Collections.unmodifiableMap(copiedValues);
        warnings = List.copyOf(new ArrayList<>(warnings));
    }

    private static Object copyValue(final Object value) {
        if(value instanceof List<?> list) return List.copyOf(list.stream().map(IrongoonConfigSnapshot::copyValue).toList());
        if(value instanceof Map<?, ?> map) {
            final Map<Object, Object> copy = new LinkedHashMap<>();
            for(final var entry : map.entrySet()) copy.put(entry.getKey(), copyValue(entry.getValue()));
            return Collections.unmodifiableMap(copy);
        }
        return value;
    }
}
