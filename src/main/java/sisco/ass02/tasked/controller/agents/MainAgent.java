package sisco.ass02.tasked.controller.agents;

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

        var velocityTasks = chunkedTasks(bodies, this::computeVelocity);
        var positionTasks = chunkedTasks(bodies, b -> b.updatePos(config.dt()));

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

    private List<Callable<Object>> chunkedTasks(List<Body> bodies, Consumer<Body> action) {
        List<Callable<Object>> tasks = new ArrayList<>();
        if (bodies.isEmpty()) return tasks;

        int chunk = config.chunkSize();
        for (int from = 0; from < bodies.size(); from += chunk) {
            List<Body> slice = bodies.subList(from, Math.min(from + chunk, bodies.size()));
            tasks.add(Executors.callable(() -> slice.forEach(action)));
        }
        return tasks;
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
