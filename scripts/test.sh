#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"
rm -rf out-test
mkdir -p out-test
find src test -name '*.java' -print0 | xargs -0 javac -encoding UTF-8 -d out-test
java -cp out-test coffeeshop.tests.TestRunner
