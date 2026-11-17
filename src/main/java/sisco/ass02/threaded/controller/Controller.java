package sisco.ass02.threaded.controller;

import sisco.ass02.config.SimulationConfig;
import sisco.ass02.threaded.controller.agents.MainAgent;
import sisco.ass02.model.Model;

public class Controller {
    public Controller(Model model, SimulationConfig config) {
        new MainAgent(model, config).start();
    }
}
