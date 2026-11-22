#!/usr/bin/env bash
# Runs JPF on the .jpf files of this folder and prints a PASS/FAIL line for each.
#
#   ./run-jpf.sh                      # ThreadedSim, ThreadedSim-edge, RacySanity (edge takes ~8 min)
#   ./run-jpf.sh ThreadedSim          # only one (name of the .jpf file, without extension)
#
# JPF_HOME defaults to /home/jpf-core (the Docker container); override it if needed.
# Full JPF output of each run is saved in build/logs/<name>.log
set -uo pipefail
cd "$(dirname "$0")"

JPF_HOME="${JPF_HOME:-/home/jpf-core}"
JAR="$JPF_HOME/build/RunJPF.jar"
[ -f "$JAR" ] || { echo "error: $JAR not found (set JPF_HOME, build JPF first)" >&2; exit 1; }

./build-jpf.sh || exit 1

# JPF on JDK 17 needs access to a JDK internal (used by its java.util.Random support)
export JAVA_TOOL_OPTIONS="--add-exports=java.base/jdk.internal.misc=ALL-UNNAMED --add-opens=java.base/jdk.internal.misc=ALL-UNNAMED"
mkdir -p build/logs

tests=("$@")
[ ${#tests[@]} -eq 0 ] && tests=(ThreadedSim ThreadedSim-edge RacySanity)

verdict() {   # $1 = test name, $2 = log file
  if [ "$1" = RacySanity ]; then
    # this one MUST fail: it proves the race detector works
    grep -q "race for field" "$2" && echo "PASS (race detected, as expected)" \
                                  || echo "FAIL: no race detected - the harness is not working"
  elif grep -q "error #" "$2"; then
    echo "FAIL: JPF reported an error (see $2)"
  elif ! grep -q "^threaded OK" "$2"; then
    echo "SUSPICIOUS: no error, but the final line 'threaded OK' was never printed (see $2)"
  elif grep -q "no errors detected" "$2"; then
    echo "PASS (no errors detected)"
  else
    echo "UNKNOWN: see $2"
  fi
}

for t in "${tests[@]}"; do
  [ -f "$t.jpf" ] || { echo "=== $t: $t.jpf not found"; continue; }
  log="build/logs/$t.log"
  echo "=== $t"
  java -jar "$JAR" "$t.jpf" > "$log" 2>&1
  grep -E "^(threaded OK|elapsed time|states:)" "$log" | sed 's/^/    /'
  echo "    -> $(verdict "$t" "$log")"
done