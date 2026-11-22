package sisco.ass02.jpf;

import sisco.ass02.config.SimulationConfig;
import sisco.ass02.model.Body;
import sisco.ass02.model.BodyFactory;
import sisco.ass02.model.Model;

import java.util.List;

/** Sequential oracle + comparison helpers shared by the JPF drivers. */
final class Reference {

    private Reference() { }

    /** Same two-phase step as the parallel versions, single-threaded. */
    static List<Body> sequential(int nBodies, long seed, int nSteps, double dt) {
        var model = new Model(BodyFactory.random(nBodies, seed));
        for (int s = 0; s < nSteps; s++) {
            for (Body b : model.bodies()) {
                var acc = model.totalForceOn(b).scalarMul(1.0 / b.getMass());
                b.updateVelocity(acc, dt);
            }
            for (Body b : model.bodies()) b.updatePos(dt);
        }
        return model.bodies();
    }

    /** Exact equality is expected: per-body summation order is identical in every version. */
    static void assertSame(List<Body> expected, List<Body> actual) {
        if (expected.size() != actual.size()) throw new AssertionError("body count differs");
        for (int i = 0; i < expected.size(); i++) {
            Body e = expected.get(i), a = actual.get(i);
            if (e.getPos().getX() != a.getPos().getX() || e.getPos().getY() != a.getPos().getY()
                    || e.getVel().getX() != a.getVel().getX() || e.getVel().getY() != a.getVel().getY()) {
                throw new AssertionError("body " + i + " differs from sequential reference: "
                        + e + " vs " + a);
            }
        }
    }

    /** args: nBodies, nThreads, nSteps (defaults 3,2,1). */
    record Args(int nBodies, int nThreads, int nSteps) {
        static Args parse(String[] a) {
            return new Args(
                    a.length > 0 ? Integer.parseInt(a[0]) : 3,
                    a.length > 1 ? Integer.parseInt(a[1]) : 2,
                    a.length > 2 ? Integer.parseInt(a[2]) : 1);
        }
        SimulationConfig config() {
            return new SimulationConfig(nThreads, nSteps).withBodies(nBodies);
        }
    }
}
