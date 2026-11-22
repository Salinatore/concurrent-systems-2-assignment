package sisco.ass02.threaded.concurrent.barrier;

public class BarrierImpl implements Barrier {

    private final int nParticipants;
    private int nArrived = 0;
    private long generation = 0;

    public BarrierImpl(int nParticipants) {
        this.nParticipants = nParticipants;
    }

    @Override
    public synchronized void hitAndWaitAll() throws InterruptedException {
        long myGeneration = generation;
        nArrived++;
        if (nArrived == nParticipants) {
            nArrived = 0;
            generation++;
            notifyAll();
        } else {
            while (myGeneration == generation) {
                wait();
            }
        }
    }
}