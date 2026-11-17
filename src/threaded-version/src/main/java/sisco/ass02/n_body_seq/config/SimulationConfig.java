package sisco.ass02.n_body_seq.config;

public record SimulationConfig(int nThreads, int nSteps, double dt, boolean testing) {

    public static final double DEFAULT_DT = 0.001;
    public static final boolean DEFAULT_TESTING = false;
    public static final int DEFAULT_N_STEPS = 1000;

    public SimulationConfig {
        if (nThreads <= 0) throw new IllegalArgumentException("nThreads must be > 0");
        if (nSteps < 0) throw new IllegalArgumentException("nSteps must be >= 0");
        if (dt <= 0) throw new IllegalArgumentException("dt must be > 0");
    }

    public SimulationConfig(int nThreads, int nSteps) {
        this(nThreads, nSteps, DEFAULT_DT, DEFAULT_TESTING);
    }

    public SimulationConfig(int nThreads, int nSteps, double dt) {
        this(nThreads, nSteps, dt, DEFAULT_TESTING);
    }

    public static SimulationConfig defaults() {
        return new SimulationConfig(Runtime.getRuntime().availableProcessors(), DEFAULT_N_STEPS);
    }

    public SimulationConfig withTesting(boolean testing) {
        return new SimulationConfig(nThreads, nSteps, dt, testing);
    }

    public int chunkSize(int nBodies) {
        int wanted = Math.min(nThreads, nBodies);
        return (int) Math.ceil(nBodies / (double) wanted);
    }

    public int effectiveAgents(int nBodies) {
        return (int) Math.ceil(nBodies / (double) chunkSize(nBodies));
    }
}