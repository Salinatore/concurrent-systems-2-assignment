package sisco.ass02.n_body_seq;

import sisco.ass02.n_body_seq.config.SimulationConfig;
import sisco.ass02.n_body_seq.controller.Controller;
import sisco.ass02.n_body_seq.model.Model;
import sisco.ass02.n_body_seq.util.BodySets;

public class SimulationMain {

    public static final int N_THREADS = 6;
    public static final int N_STEPS = 1000;
    public static final int N_BODIES = 1000;
    public static final int SEED = 42;

    static void main() {
        var bodies = BodySets.random(N_BODIES, SEED);
        var config = new SimulationConfig(N_THREADS, N_STEPS).withTesting(true);
        var model = new Model(bodies);

        new Controller(model, config);
    }
}
