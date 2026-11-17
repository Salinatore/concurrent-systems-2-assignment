package sisco.ass02.threaded.model;

import java.util.List;
import java.util.Objects;

public record Model(List<Body> bodies) {

    public Model {
        Objects.requireNonNull(bodies, "bodies");
        bodies = List.copyOf(bodies);
    }
}