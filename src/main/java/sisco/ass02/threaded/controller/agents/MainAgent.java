package sisco.ass02.threaded.controller.agents;

import sisco.ass02.threaded.concurrent.barrier.Barrier;
import sisco.ass02.threaded.concurrent.barrier.BarrierImpl;
import sisco.ass02.config.SimulationConfig;
import sisco.ass02.model.Model;

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
        int chunkSize = config.chunkSize();

        var chunks = bodies.stream()
                .gather(Gatherers.windowFixed(chunkSize))
                .toList();

        Barrier firstBarrier = new BarrierImpl(chunks.size());
        Barrier secondBarrier = new BarrierImpl(chunks.size());

        var agents = chunks.stream()
                .map(chunk -> new ComputeAgent(model, firstBarrier, secondBarrier, config, chunk))
                .toList();

        long startTime = System.nanoTime();
        log("Starting threaded simulation with " + bodies.size() + " bodies - "  + config.nThreads() + " threads - " + config.nSteps() + " steps...");

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
