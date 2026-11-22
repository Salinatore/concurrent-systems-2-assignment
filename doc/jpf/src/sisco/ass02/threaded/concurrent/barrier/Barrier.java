package sisco.ass02.threaded.concurrent.barrier;

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

