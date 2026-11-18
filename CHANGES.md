# Changes

## Removed duplication

- **`Model.totalForceOn(Body)`**: the total-force computation was copy-pasted in
  `threaded.ComputeAgent` and `tasked.MainAgent`. It now lives in `Model`, is read-only,
  and only skips pairs that throw `InfiniteForceException` (previously every `Exception`
  was swallowed).
- **`util.Partition.chunks(items, nParts)`**: one shared way to split the bodies, used by
  both implementations. It creates *at most* `nParts` chunks, so callers must size
  synchronization from the returned list, not from `nParts`.
    - `SimulationConfig.chunkSize()` and the hand-written `subList` loop in the tasked
      version were removed.
    - `SimulationConfig.withSeed` was dropped as well (not used anywhere).

## Threaded version

- A single `BarrierImpl` is now used twice per step (reads done, then writes done)
  instead of two separate barrier objects. It is created with `chunks.size()` participants.
- `ComputeAgent` receives the force function (`model::totalForceOn`) instead of the
  whole `Model`.

## Tasked version

- The thread pool is sized `chunks.size()` (the number of tasks) instead of `nThreads`.
- `chunkedTasks` was replaced by tasks built directly from `Partition.chunks`.

## Cleanup

- Removed unused imports in both `MainAgent` classes.
