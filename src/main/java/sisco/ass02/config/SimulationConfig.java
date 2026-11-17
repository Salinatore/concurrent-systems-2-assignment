package sisco.ass02.config;

public record SimulationConfig(
        int nThreads,
        int nSteps,
        double dt,
        boolean testing,
        int nBodies,
        long seed
) {

    public static final int DEFAULT_N_STEPS = 1000;
    public static final double DEFAULT_DT = 0.001;
    public static final boolean DEFAULT_TESTING = false;
    public static final int DEFAULT_N_BODIES = 1000;
    public static final long DEFAULT_SEED = 42L;

    public SimulationConfig {
        if (nThreads <= 0) throw new IllegalArgumentException("nThreads must be > 0");
        if (nSteps < 0) throw new IllegalArgumentException("nSteps must be >= 0");
        if (dt <= 0) throw new IllegalArgumentException("dt must be > 0");
        if (nBodies <= 0) throw new IllegalArgumentException("nBodies must be > 0");
    }

    public SimulationConfig(int nThreads, int nSteps) {
        this(nThreads, nSteps, DEFAULT_DT);
    }

    public SimulationConfig(int nThreads, int nSteps, double dt) {
        this(nThreads, nSteps, dt, DEFAULT_TESTING, DEFAULT_N_BODIES, DEFAULT_SEED);
    }

    public static SimulationConfig defaults() {
        return new SimulationConfig(Runtime.getRuntime().availableProcessors(), DEFAULT_N_STEPS);
    }

    public SimulationConfig withThreads(int nThreads) {
        return new SimulationConfig(nThreads, nSteps, dt, testing, nBodies, seed);
    }

    public SimulationConfig withSteps(int nSteps) {
        return new SimulationConfig(nThreads, nSteps, dt, testing, nBodies, seed);
    }

    public SimulationConfig withTesting(boolean testing) {
        return new SimulationConfig(nThreads, nSteps, dt, testing, nBodies, seed);
    }

    public SimulationConfig withBodies(int nBodies) {
        return new SimulationConfig(nThreads, nSteps, dt, testing, nBodies, seed);
    }

    public SimulationConfig withSeed(long seed) {
        return new SimulationConfig(nThreads, nSteps, dt, testing, nBodies, seed);
    }

    /** Number of bodies per task so that at most nThreads tasks are created. */
    public int chunkSize() {
        int tasks = Math.min(nThreads, nBodies);
        return (nBodies + tasks - 1) / tasks;   // integer ceil division
    }
}