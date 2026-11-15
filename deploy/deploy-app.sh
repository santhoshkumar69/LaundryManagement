#!/usr/bin/env bash
# Copy built API JAR, Angular dist, and optional DB dump to the droplet, then restart services.
# Usage: LAUNDRY_IP=x.x.x.x ./deploy-app.sh [/path/to/dump.sql.gz]
set -euo pipefail

IP="${LAUNDRY_IP:?Set LAUNDRY_IP}"
SSH_USER="${SSH_USER:-root}"
SSH=(ssh -o StrictHostKeyChecking=accept-new -o ConnectTimeout=15 "$SSH_USER@$IP")
SCP=(scp -o StrictHostKeyChecking=accept-new)
ROOT_API="$(cd "$(dirname "$0")/.." && pwd)"
ROOT_UI="${ROOT_UI:-$(cd "$ROOT_API/../../upgrade/laundryui-ng17" 2>/dev/null && pwd || true)}"
if [[ -z "${ROOT_UI}" || ! -d "$ROOT_UI" ]]; then
  ROOT_UI="$(cd "$ROOT_API/../laundryui-ng17" && pwd)"
fi
JAR="$(ls -1 "$ROOT_API"/target/LaundaryManagement-*.jar 2>/dev/null | head -1 || true)"
UI_DIST="$ROOT_UI/dist/LaundryUI"
DUMP="${1:-}"

if [[ -z "$JAR" || ! -f "$JAR" ]]; then
  echo "Missing API JAR. Build with: mvn -DskipTests package" >&2
  exit 1
fi
if [[ ! -d "$UI_DIST" ]]; then
  echo "Missing UI dist at $UI_DIST. Build with: ng build --configuration production" >&2
  exit 1
fi

echo "Uploading API jar..."
"${SCP[@]}" "$JAR" "$SSH_USER@$IP:/opt/laundry/laundry-api.jar"
"${SSH[@]}" "chown laundry:laundry /opt/laundry/laundry-api.jar"

echo "Uploading UI..."
"${SSH[@]}" "rm -rf /var/www/laundryui/*"
"${SCP[@]}" -r "$UI_DIST"/. "$SSH_USER@$IP:/var/www/laundryui/"
"${SSH[@]}" "chown -R caddy:caddy /var/www/laundryui || chown -R www-data:www-data /var/www/laundryui"

if [[ -n "$DUMP" && -f "$DUMP" ]]; then
  echo "Restoring database dump $DUMP..."
  "${SCP[@]}" "$DUMP" "$SSH_USER@$IP:/opt/laundry/backups/restore.sql.gz"
  "${SSH[@]}" 'gunzip -c /opt/laundry/backups/restore.sql.gz | mysql -ulaundry -p"$(grep SPRING_DATASOURCE_PASSWORD /opt/laundry/laundry.env | cut -d= -f2-)" laundryservice'
fi

"${SSH[@]}" "systemctl restart laundry-api caddy; systemctl --no-pager --full status laundry-api | head -20"
echo "Deployed to https://${IP}.sslip.io"
