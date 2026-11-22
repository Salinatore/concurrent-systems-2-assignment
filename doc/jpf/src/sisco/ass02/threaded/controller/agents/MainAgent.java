package sisco.ass02.threaded.controller.agents;

import sisco.ass02.config.SimulationConfig;
import sisco.ass02.model.Model;
import sisco.ass02.threaded.concurrent.barrier.Barrier;
import sisco.ass02.threaded.concurrent.barrier.BarrierImpl;
import sisco.ass02.util.Partition;

import java.util.stream.Collectors;

/**
 * JPF-compat copy of the threaded MainAgent. Only differences from the real one:
 * Stream.toList() -> Collectors.toList() (JPF cannot run Stream.toList), and logging removed.
 * The wiring (barrier sized from chunks.size(), one ComputeAgent per chunk) is unchanged.
 */
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
                .collect(Collectors.toList());

        agents.forEach(Thread::start);

        try {
            for (var agent : agents) {
                agent.join();
            }
        } catch (InterruptedException e) {
            agents.forEach(Thread::interrupt);
            Thread.currentThread().interrupt();
        }
    }
}
