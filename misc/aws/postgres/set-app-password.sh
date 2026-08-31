#!/usr/bin/env bash
set -euo pipefail

psql --set ON_ERROR_STOP=1 --dbname=postgres \
  --command="ALTER ROLE sub3 LOGIN PASSWORD '${POSTGRES_APP_PASSWORD:-sub3}';"
