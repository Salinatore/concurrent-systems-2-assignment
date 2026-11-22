# Testing with Java PathFinder

## Why there is a `compat/` folder
JPF (jpf-core `java-17`/`master`) can only execute Java <= 17 bytecode and must itself run on JDK 17.
The project targets Java 25, so `build-jpf.sh` compiles everything with `javac --release 17`, overlaying
four spots that don't exist in 17 (`compat/` copies, same logic):

| File | Java 25 feature replaced |
|---|---|
| `model/Model.java` | unnamed variable `catch (... _)` (22); `List.copyOf` (JPF cannot run it) |
| `util/Partition.java` | `Gatherers.windowFixed` (24), `Math.ceilDiv` (18), `List.of/copyOf` |
| `tasked/.../MainAgent.java` | **JPF cannot run `ThreadPoolExecutor`/`FutureTask`** (VarHandle.setRelease not modelled), so `invokeAll` is replaced by a stand-in (one thread per task, then join); `Stream.toList()`; logging removed |
| `threaded/.../MainAgent.java` | `Stream.toList()` (JPF cannot run it); logging removed |

Everything else (Body, V2d, BarrierImpl, ComputeAgent) is the real code.

## Setup (once)
    git clone https://github.com/javapathfinder/jpf-core.git && cd jpf-core
    ./gradlew buildJars          # with JDK 17 active
    export JPF_HOME=$PWD

## Run
    cd jpf
    ./build-jpf.sh               # any JDK >= 17
    JPF_HOME=... ./run-jpf.sh    # or: java -jar $JPF_HOME/build/RunJPF.jar ThreadedSim.jpf

| Config | What it checks |
|---|---|
| `TestSequential.jpf`, `TestScenarios.jpf` | the course examples (lab-activity-06), run from here. Warm-up: Sequential = 1 path; Scenarios = 2 threads, JPF prints every interleaving of a1,a2 / b1,b2 and finds no race |
| `Barrier.jpf` | BarrierImpl reused over several rounds: nobody leaves round r before all arrived |
| `ThreadedSim.jpf` | real threaded MainAgent, 3 bodies / 2 threads / 1 step: no deadlock, no race, result == sequential |
| `ThreadedSim-edge.jpf` | nThreads (4) > nBodies (3): Partition gives 3 chunks; a barrier sized 4 would deadlock |
| `TaskedSim.jpf` | tasked version with the stand-in `invokeAll`: checks the two-phase task structure, NOT the real executor |
| `RacySanity.jpf` | **must report an error**: barrier replaced by a no-op. Proves race detection works |

Change sizes via `target.args = nBodies,nThreads,nSteps` in the .jpf file. Keep them tiny
(2-4 bodies, 1-2 steps): JPF explores interleavings exhaustively.

## Reading results
- `no errors detected` on the 4 real configs **and** an error on `RacySanity` = good.
- `deadlock` -> `DeadlockAnalyzer` prints which thread waits on what.
- `data race` -> `PreciseRaceDetector` names the field and both threads.
- This is exhaustive only for the tiny instance and for what JPF models; it is not a proof for 1000 bodies.

## If something goes wrong
- `Unsupported class file version`: JPF is running on, or classes were compiled for, a JDK > 17.
- `TaskedSim` too slow/out of memory: raise heap (`java -Xmx8g -jar ...`) or add `+vm.por=true`
  (default) / lower to `2,2,1`. JPF's support for ThreadPoolExecutor is the usual culprit, not your code.
- Spurious races inside JDK classes: widen `race.exclude` in the .jpf file.
