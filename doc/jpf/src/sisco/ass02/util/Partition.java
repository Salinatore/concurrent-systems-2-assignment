package sisco.ass02.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JPF-compat copy: same contract as the real Partition (consecutive chunks, at most nParts,
 * chunk size = ceil(n / min(nParts, n))), but without Gatherers (Java 24) and Math.ceilDiv (Java 18).
 */
public final class Partition {

    private Partition() { }

    public static <T> List<List<T>> chunks(List<T> items, int nParts) {
        if (nParts <= 0) throw new IllegalArgumentException("nParts must be > 0");
        if (items.isEmpty()) return new ArrayList<>();

        int parts = Math.min(nParts, items.size());
        int chunkSize = (items.size() + parts - 1) / parts;

        var out = new ArrayList<List<T>>();
        for (int i = 0; i < items.size(); i += chunkSize) {
            out.add(Collections.unmodifiableList(new ArrayList<>(items.subList(i, Math.min(i + chunkSize, items.size())))));
        }
        return out;
    }
}
