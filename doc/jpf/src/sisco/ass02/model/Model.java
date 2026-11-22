package sisco.ass02.model;

import sisco.ass02.model.exception.InfiniteForceException;
import sisco.ass02.model.geometry.V2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** JPF-compat copy: identical to the real Model, but without the unnamed variable `_` (Java 22). */
public record Model(List<Body> bodies) {

    public Model {
        Objects.requireNonNull(bodies, "bodies");
        bodies = Collections.unmodifiableList(new ArrayList<>(bodies)); // List.copyOf breaks JPF
    }

    public V2d totalForceOn(Body b) {
        var total = new V2d(0, 0);
        for (Body other : bodies) {
            if (!b.equals(other)) {
                try {
                    total.sum(b.computeGForceBy(other));
                } catch (InfiniteForceException ignored) {
                    // coincident bodies: skip this pair
                }
            }
        }
        return total;
    }
}
