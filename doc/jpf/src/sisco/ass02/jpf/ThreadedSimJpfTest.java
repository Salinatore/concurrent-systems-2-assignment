package sisco.ass02.jpf;

import sisco.ass02.model.BodyFactory;
import sisco.ass02.model.Model;
import sisco.ass02.threaded.controller.agents.MainAgent;

/**
 * Runs the REAL threaded MainAgent (BarrierImpl + ComputeAgent + Partition) on a tiny instance.
 * JPF explores thread interleavings and checks: no deadlock (e.g. barrier sized wrongly),
 * no uncaught exception, no data race (listener), and result == sequential reference.
 */
public class ThreadedSimJpfTest {
    public static void main(String[] args) throws Exception {
        var a = Reference.Args.parse(args);
        var cfg = a.config();

        var expected = Reference.sequential(a.nBodies(), cfg.seed(), a.nSteps(), cfg.dt());

        var model = new Model(BodyFactory.random(a.nBodies(), cfg.seed()));
        var agent = new MainAgent(model, cfg);
        agent.start();
        agent.join();

        Reference.assertSame(expected, model.bodies());
        System.out.println("threaded OK " + a.nBodies() + "," + a.nThreads() + "," + a.nSteps());
    }
}
