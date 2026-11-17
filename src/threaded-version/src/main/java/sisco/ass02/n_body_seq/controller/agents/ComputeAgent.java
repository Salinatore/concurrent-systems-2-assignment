package sisco.ass02.n_body_seq.controller.agents;

import sisco.ass02.n_body_seq.concurrent.barrier.Barrier;
import sisco.ass02.n_body_seq.config.SimulationConfig;
import sisco.ass02.n_body_seq.model.Body;
import sisco.ass02.n_body_seq.model.Model;
import sisco.ass02.n_body_seq.model.geometry.V2d;

import java.util.List;

public class ComputeAgent extends Thread {

    private final List<Body> bodies;
    private final List<Body> chunk;
    private final long nSteps;
    private final Barrier firstBarrier;
    private final Barrier secondBarrier;
    private final double dt;

    public ComputeAgent(Model model, SimulationConfig config, List<Body> chunk) {
        this.bodies = model.bodies();
        this.firstBarrier = model.firstBarrier();
        this.secondBarrier = model.secondBarrier();
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
                V2d totalForce = computeTotalGForceOnBody(b);
                V2d acc = new V2d(totalForce).scalarMul(1.0 / b.getMass());
                b.updateVelocity(acc, dt);
            }

            try {
                firstBarrier.hitAndWaitAll();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            for (Body b : chunk) {
                b.updatePos(dt);
            }

            try {
                secondBarrier.hitAndWaitAll();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            vt = vt + dt;
            iter++;
        }
    }

    /*
     *
     * Compute the total G force exerted on a body by the other bodies
     *
     */
    private V2d computeTotalGForceOnBody(Body b) {
        V2d totalForce = new V2d(0, 0);
        for (Body otherBody : bodies) {
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
}
