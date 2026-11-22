package sisco.ass02.jpf;

import sisco.ass02.config.SimulationConfig;
import sisco.ass02.model.BodyFactory;
import sisco.ass02.model.Model;
import sisco.ass02.model.Body;
import sisco.ass02.threaded.concurrent.barrier.Barrier;
import sisco.ass02.threaded.controller.agents.ComputeAgent;
import sisco.ass02.util.Partition;

import java.util.List;
import java.util.stream.Collectors;

/**
 * HARNESS SANITY CHECK - JPF is EXPECTED TO REPORT AN ERROR HERE.
 * Same wiring as the threaded MainAgent but with a barrier that does not block, so one thread
 * can write positions while another still reads them. If JPF reports nothing, the race
 * detection setup is not working and a clean result on the real tests means nothing.
 */
public class RacySanityJpfTest {

    static final class NoopBarrier implements Barrier {
        @Override public void hitAndWaitAll() { }
    }

    public static void main(String[] args) throws Exception {
        var cfg = new SimulationConfig(2, 1).withBodies(3);
        var model = new Model(BodyFactory.random(3, cfg.seed()));

        List<List<Body>> chunks = Partition.chunks(model.bodies(), cfg.nThreads());
        Barrier broken = new NoopBarrier();
        var agents = chunks.stream()
                .map(c -> new ComputeAgent(model::totalForceOn, broken, cfg, c))
                .collect(Collectors.toList());

        agents.forEach(Thread::start);
        for (var t : agents) t.join();
    }
}
