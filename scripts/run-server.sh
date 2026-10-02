#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"
bash scripts/build.sh
echo "Open http://localhost:8080"
java --add-modules jdk.httpserver -cp out coffeeshop.CoffeeShopServer
