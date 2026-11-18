package sisco.ass02.tasked.controller.agents;

import sisco.ass02.concurrent.Partition;
import sisco.ass02.model.Body;
import sisco.ass02.model.geometry.V2d;
import sisco.ass02.config.SimulationConfig;
import sisco.ass02.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

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

        long startTime = System.nanoTime();
        log("Starting tasked simulation with " + bodies.size() + " bodies - "  + config.nThreads() + " threads - " + config.nSteps() + " steps...");

        var chunks = Partition.chunks(bodies, config.nThreads());

        var velocityTasks = chunks.stream()
                .map(c -> Executors.callable(() -> c.forEach(this::computeVelocity)))
                .toList();
        var positionTasks = chunks.stream()
                .map(c -> Executors.callable(() -> c.forEach(b -> b.updatePos(config.dt()))))
                .toList();

        try (ExecutorService executor = Executors.newFixedThreadPool(config.nThreads())) {
            for (int i = 0; i < config.nSteps(); i++) {
                executor.invokeAll(velocityTasks);
                executor.invokeAll(positionTasks);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        if (config.testing()) {
            bodies.stream().sorted().forEach(System.out::println);
        }

        long elapsedNs = System.nanoTime() - startTime;
        log(String.format("Simulation completed in %.3f s", elapsedNs / 1e9));
    }

    private void computeVelocity(Body b) {
        V2d totalForce = computeTotalGForceOnBody(b);
        V2d acc = new V2d(totalForce).scalarMul(1.0 / b.getMass());
        b.updateVelocity(acc, this.config.dt());
    }

    /*
     *
     * Compute the total G force exerted on a body by the other bodies
     *
     */
    private V2d computeTotalGForceOnBody(Body b) {
        V2d totalForce = new V2d(0, 0);
        for (int j = 0; j < this.model.bodies().size(); j++) {
            Body otherBody = this.model.bodies().get(j);
            if (!b.equals(otherBody)) {
                try {
                    V2d forceByOtherBody = b.computeGForceBy(otherBody);
                    totalForce.sum(forceByOtherBody);
                } catch (Exception _) {
                }
            }
        }
        return totalForce;
    }

    private static void log(String msg) {
        System.out.println("[" + System.currentTimeMillis() + "] " + msg);
    }
}
