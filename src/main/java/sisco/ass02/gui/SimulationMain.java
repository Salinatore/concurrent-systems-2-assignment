package sisco.ass02.gui;

import sisco.ass02.config.SimulationConfig;
import sisco.ass02.gui.view.SimulationView;
import sisco.ass02.tasked.controller.Controller;
import sisco.ass02.model.Model;
import sisco.ass02.model.BodyFactory;

public class SimulationMain {

    static void main() {
        var config = SimulationConfig.defaults();
        var bodies = BodyFactory.random(config.nBodies(), config.seed());
        var model = new Model(bodies);
        var view = new SimulationView(620, 620);

        new Controller(model, config);
    }
}
