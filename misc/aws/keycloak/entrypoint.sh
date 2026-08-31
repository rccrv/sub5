#!/usr/bin/env bash
set -euo pipefail

template=/opt/keycloak/data/import/sub3-realm-template.json
realm=/opt/keycloak/data/import/sub3-realm.json
principal_secret="${KEYCLOAK_PRINCIPAL_CLIENT_SECRET:-jfkMfo25oWyJHHB9MAc3pwJ7k1ZKzK1L}"

# Keep the checked-in realm structure while allowing the ECS secret to replace
# the application client secret before Keycloak imports the realm.
sed "s|jfkMfo25oWyJHHB9MAc3pwJ7k1ZKzK1L|${principal_secret}|g" "$template" > "$realm"
exec /opt/keycloak/bin/kc.sh "$@"
