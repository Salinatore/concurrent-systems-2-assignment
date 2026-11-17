package sisco.ass02.n_body_seq.concurrent.barrier;

/**
 *
 * Barrier behavior
 *
 * N agents blocks until they *all* arrive to a common sync point ("barrier")
 *
 */
public interface Barrier {

    void hitAndWaitAll() throws InterruptedException;
}

