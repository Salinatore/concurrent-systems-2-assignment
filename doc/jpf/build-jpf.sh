#!/usr/bin/env bash
# Compiles jpf/src into jpf/build/classes as Java 17 bytecode
# (JPF cannot read class files newer than Java 17).
# Needs a JDK >= 17 on PATH. Run from anywhere: ./build-jpf.sh
set -euo pipefail
cd "$(dirname "$0")"

ver=$(javac -version 2>&1 | grep -E '^javac' | grep -oE '[0-9]+' | head -1)
if [ -z "${ver:-}" ] || [ "$ver" -lt 17 ]; then
  echo "error: javac ${ver:-not found}: a JDK >= 17 is required (--release 17)" >&2
  exit 1
fi

rm -rf build && mkdir -p build/classes
find src -name '*.java' > build/sources.txt

# env -u: the JPF --add-exports flags (JAVA_TOOL_OPTIONS) are for running JPF, not for compiling
env -u JAVA_TOOL_OPTIONS javac --release 17 -d build/classes @build/sources.txt
echo "compiled $(wc -l < build/sources.txt) files into build/classes"