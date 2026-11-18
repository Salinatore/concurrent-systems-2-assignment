package sisco.ass02.model;

import sisco.ass02.model.exception.InfiniteForceException;
import sisco.ass02.model.geometry.V2d;

import java.util.List;
import java.util.Objects;

public record Model(List<Body> bodies) {

    public Model {
        Objects.requireNonNull(bodies, "bodies");
        bodies = List.copyOf(bodies);
    }

    /**
     * Total gravitational force exerted on b by all the other bodies.
     * <p>
     * Read-only: it only reads positions and masses, so it can be called
     * concurrently as long as nobody is writing positions (phase 1 of a step).
     * Pairs at zero distance contribute no force.
     */
    public V2d totalForceOn(Body b) {
        var total = new V2d(0, 0);
        for (Body other : bodies) {
            if (!b.equals(other)) {
                try {
                    total.sum(b.computeGForceBy(other));
                } catch (InfiniteForceException _) {
                    // coincident bodies: skip this pair
                }
            }
        }
        return total;
    }
}