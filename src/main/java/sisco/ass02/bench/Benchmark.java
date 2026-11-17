package sisco.ass02.bench;

import sisco.ass02.config.SimulationConfig;
import sisco.ass02.model.BodyFactory;
import sisco.ass02.model.Model;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class Benchmark {

    private static final int N_BODIES = 1000;
    private static final int N_STEPS  = 200;   // lower than default so the benchmark finishes quickly
    private static final int WARMUP   = 1;
    private static final int RUNS     = 5;

    record Impl(String name, BiFunction<Model, SimulationConfig, Thread> factory) { }

    static void main() throws InterruptedException {
        var impls = List.of(
                new Impl("threaded", sisco.ass02.threaded.controller.agents.MainAgent::new),
                new Impl("tasked",   sisco.ass02.tasked.controller.agents.MainAgent::new)
        );

        var threadCounts = threadCounts();
        var base = SimulationConfig.defaults().withBodies(N_BODIES).withSteps(N_STEPS);

        PrintStream out = System.out;
        System.out.printf("bodies=%d steps=%d warmup=%d runs=%d cores=%d%n%n",
                N_BODIES, N_STEPS, WARMUP, RUNS, Runtime.getRuntime().availableProcessors());
        System.out.printf("%-10s %8s %12s %12s %10s%n", "impl", "threads", "median (ms)", "min (ms)", "speedup");

        for (var impl : impls) {
            double baseline = -1;
            for (int t : threadCounts) {
                var config = base.withThreads(t);
                System.setOut(new PrintStream(OutputStream.nullOutputStream())); // silence agent logs
                double[] times = new double[RUNS];
                try {
                    for (int i = 0; i < WARMUP; i++) runOnce(impl, config);
                    for (int i = 0; i < RUNS; i++)   times[i] = runOnce(impl, config);
                } finally {
                    System.setOut(out);
                }

                Arrays.sort(times);
                double median = times[RUNS / 2];
                if (baseline < 0) baseline = median;   // first entry (1 thread) is the baseline
                out.printf("%-10s %8d %12.1f %12.1f %9.2fx%n",
                        impl.name(), t, median, times[0], baseline / median);
            }
            out.println();
        }
    }

    /** One full simulation on a fresh model; returns elapsed milliseconds. */
    private static double runOnce(Impl impl, SimulationConfig config) throws InterruptedException {
        var model = new Model(BodyFactory.random(config.nBodies(), config.seed()));
        var agent = impl.factory().apply(model, config);

        long start = System.nanoTime();
        agent.start();
        agent.join();
        return (System.nanoTime() - start) / 1e6;
    }

    private static List<Integer> threadCounts() {
        int cores = Runtime.getRuntime().availableProcessors();
        var counts = new ArrayList<Integer>();
        for (int t = 1; t < cores; t *= 2) counts.add(t);
        counts.add(cores);
        return counts;
    }
}