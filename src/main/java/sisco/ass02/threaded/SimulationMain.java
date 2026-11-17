package sisco.ass02.threaded;

import sisco.ass02.config.SimulationConfig;
import sisco.ass02.threaded.controller.Controller;
import sisco.ass02.model.Model;
import sisco.ass02.util.BodySets;

public class SimulationMain {

    static void main() {
        var config = SimulationConfig.defaults();
        var bodies = BodySets.random(config.nBodies(), config.seed());
        var model = new Model(bodies);

        new Controller(model, config);
    }
}
