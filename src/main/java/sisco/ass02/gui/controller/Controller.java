package sisco.ass02.gui.controller;

import sisco.ass02.config.SimulationConfig;
import sisco.ass02.model.Model;
import sisco.ass02.threaded.controller.agents.MainAgent;

public class Controller {
    public Controller(Model model, SimulationConfig config) {
        new MainAgent(model, config).start();
    }
}
