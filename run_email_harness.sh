#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

if ! command -v javac >/dev/null 2>&1; then
  echo "javac is required to compile EmailGeneratorHarness.java" >&2
  exit 1
fi

javac EmailGeneratorHarness.java

java EmailGeneratorHarness "$@"
