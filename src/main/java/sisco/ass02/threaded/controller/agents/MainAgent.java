package sisco.ass02.threaded.controller.agents;

import sisco.ass02.util.Partition;
import sisco.ass02.threaded.concurrent.barrier.Barrier;
import sisco.ass02.threaded.concurrent.barrier.BarrierImpl;
import sisco.ass02.config.SimulationConfig;
import sisco.ass02.model.Model;

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

        var chunks = Partition.chunks(bodies, config.nThreads());

        Barrier barrier = new BarrierImpl(chunks.size());

        var agents = chunks.stream()
                .map(chunk -> new ComputeAgent(model::totalForceOn, barrier, config, chunk))
                .toList();

        long startTime = System.nanoTime();
        log("Starting threaded simulation with " + bodies.size() + " bodies - "  + chunks.size() + " threads - " + config.nSteps() + " steps...");

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

        long elapsedNs = System.nanoTime() - startTime;
        log(String.format("Simulation completed in %.3f s", elapsedNs / 1e9));
    }

    private static void log(String msg) {
        System.out.println("[" + System.currentTimeMillis() + "] " + msg);
    }
}
