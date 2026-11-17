package sisco.ass02.n_body_seq.model;

import sisco.ass02.n_body_seq.concurrent.barrier.Barrier;
import sisco.ass02.n_body_seq.concurrent.barrier.BarrierImpl;
import sisco.ass02.n_body_seq.config.SimulationConfig;

import java.util.List;
import java.util.Objects;

public record Model(List<Body> bodies, Barrier firstBarrier, Barrier secondBarrier) {

    public Model {
        Objects.requireNonNull(bodies, "bodies");
        Objects.requireNonNull(firstBarrier, "firstBarrier");
        Objects.requireNonNull(secondBarrier, "secondBarrier");
        bodies = List.copyOf(bodies); // defensive copy, immutable, rejects null elements
    }

    /** Builds the barriers sized to the number of agents that will actually run. */
    public Model(List<Body> bodies, SimulationConfig config) {
        this(bodies,
                new BarrierImpl(config.effectiveAgents(bodies.size())),
                new BarrierImpl(config.effectiveAgents(bodies.size())));
    }
}