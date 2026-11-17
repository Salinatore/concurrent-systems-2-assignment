package sisco.ass02.n_body_seq.controller;

import sisco.ass02.n_body_seq.config.SimulationConfig;
import sisco.ass02.n_body_seq.controller.agents.MainAgent;
import sisco.ass02.n_body_seq.model.Model;

public class Controller {
    public Controller(Model model, SimulationConfig config) {
        new MainAgent(model, config).start();
    }
}
