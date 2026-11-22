package sisco.ass02.threaded.controller.agents;

import sisco.ass02.threaded.concurrent.barrier.Barrier;
import sisco.ass02.config.SimulationConfig;
import sisco.ass02.model.Body;
import sisco.ass02.model.Model;
import sisco.ass02.model.geometry.V2d;

import java.util.List;
import java.util.function.Function;

public class ComputeAgent extends Thread {

    private final Function<Body, V2d> totalForceOn;
    private final List<Body> chunk;
    private final long nSteps;
    private final Barrier barrier;
    private final double dt;

    public ComputeAgent(
            Function<Body, V2d> totalForceOn,
            Barrier barrier,
            SimulationConfig config,
            List<Body> chunk)
    {
        this.totalForceOn = totalForceOn;
        this.barrier = barrier;
        this.nSteps = config.nSteps();
        this.chunk = chunk;
        this.dt = config.dt();
    }

    @Override
    public void run() {
        double vt = 0;
        long iter = 0;

        while (iter < nSteps) {
            for (Body b : chunk) {
                V2d totalForce = this.totalForceOn.apply(b);
                V2d acc = new V2d(totalForce).scalarMul(1.0 / b.getMass());
                b.updateVelocity(acc, dt);
            }

            try {
                barrier.hitAndWaitAll();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            for (Body b : chunk) {
                b.updatePos(dt);
            }

            try {
                barrier.hitAndWaitAll();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            vt = vt + dt;
            iter++;
        }
    }
}
