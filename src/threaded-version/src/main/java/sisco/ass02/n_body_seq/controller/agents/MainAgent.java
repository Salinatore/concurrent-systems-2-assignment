package sisco.ass02.n_body_seq.controller.agents;

import sisco.ass02.n_body_seq.config.SimulationConfig;
import sisco.ass02.n_body_seq.model.Model;

import java.util.List;
import java.util.stream.Gatherers;

public class MainAgent extends Thread {

    private final Model model;
    private final SimulationConfig config;

    public MainAgent(Model model, SimulationConfig config) {
        this.model = model;
        this.config = config;
    }

    @Override
    public void run() {
        var bodies = model.bodies();
        int chunkSize = config.chunkSize(bodies.size());

        List<ComputeAgent> agents = bodies.stream()
                .gather(Gatherers.windowFixed(chunkSize))
                .map(chunk -> new ComputeAgent(model, config, chunk))
                .toList();

        long startTime = System.currentTimeMillis();
        log("Starting simulation with " + bodies.size() + " bodies - "  + config.nThreads() + " threads - " + config.nSteps() + " steps...");

        agents.forEach(Thread::start);

        try {
            for (var agent : agents) {
                agent.join();
            }
        } catch (InterruptedException e) {
            agents.forEach(Thread::interrupt);
            Thread.currentThread().interrupt();
        }

        if (config.testing()) {
            bodies.stream().sorted().forEach(System.out::println);
        }

        long endTime = System.currentTimeMillis();
        log("Simulation completed in " + (endTime - startTime) + " millisecond.");
    }

    private static void log(String msg) {
        System.out.println("[" + System.currentTimeMillis() + "] " + msg);
    }
}
