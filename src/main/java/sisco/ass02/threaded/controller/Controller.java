package sisco.ass02.threaded.controller;

import sisco.ass02.threaded.config.SimulationConfig;
import sisco.ass02.threaded.controller.agents.MainAgent;
import sisco.ass02.threaded.model.Model;

public class Controller {
    public Controller(Model model, SimulationConfig config) {
        new MainAgent(model, config).start();
    }
}
