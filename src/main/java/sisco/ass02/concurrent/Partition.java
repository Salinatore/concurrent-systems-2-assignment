package sisco.ass02.concurrent;

import java.util.List;
import java.util.stream.Gatherers;

public final class Partition {

    private Partition() { }

    /**
     * Splits items into consecutive chunks, creating at most nParts of them.
     * <p>
     * The chunk size is ceil(items.size() / nParts), so the actual number of chunks
     * can be lower than nParts (e.g. 6 items in 4 parts gives 3 chunks of 2).
     * Callers must size any synchronization (barriers, latches) from the
     * returned list's size, never from nParts.
     * <p>
     * Chunks hold the same element references as the input, so mutating the
     * elements is visible to everyone; only the chunk lists themselves are immutable.
     *
     * @return the chunks, or an empty list if items is empty
     */
    public static <T> List<List<T>> chunks(List<T> items, int nParts) {
        if (nParts <= 0) throw new IllegalArgumentException("nParts must be > 0");
        if (items.isEmpty()) return List.of();

        int parts = Math.min(nParts, items.size());
        int chunkSize = Math.ceilDiv(items.size(), parts);

        return items.stream()
                .gather(Gatherers.windowFixed(chunkSize))
                .toList();
    }
}