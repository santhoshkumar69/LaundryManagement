#!/usr/bin/env bash
# End-to-end: auth → droplet → bootstrap → deploy JAR/UI → restore DB dump.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

TOKEN_FILE="$ROOT/deploy/.do_token"
if [[ -z "${DIGITALOCEAN_ACCESS_TOKEN:-}" && -f "$TOKEN_FILE" ]]; then
  DIGITALOCEAN_ACCESS_TOKEN="$(tr -d ' \n\r' < "$TOKEN_FILE")"
  export DIGITALOCEAN_ACCESS_TOKEN
fi
if [[ -z "${DIGITALOCEAN_ACCESS_TOKEN:-}" ]]; then
  echo "Missing DigitalOcean token." >&2
  echo "Create one at https://cloud.digitalocean.com/account/api/tokens" >&2
  echo "Then:  echo 'dop_v1_...' > deploy/.do_token" >&2
  exit 1
fi

DB_PASSWORD="${DB_PASSWORD:-$(openssl rand -hex 16)}"
AUTH_SECRET="${AUTH_SECRET:-$(openssl rand -hex 32)}"
ACME_EMAIL="${LAUNDRY_ACME_EMAIL:-laundry@$(whoami).local}"
export DB_PASSWORD AUTH_SECRET

DUMP="$ROOT/deploy/laundryservice.sql.gz"
if [[ ! -f "$DUMP" ]]; then
  echo "Creating DB dump..."
  /usr/local/mysql/bin/mysqldump -h127.0.0.1 -uroot -pChennai123 --single-transaction --routines --triggers laundryservice \
    | gzip -c > "$DUMP"
fi

if [[ ! -f "$ROOT/target/LaundaryManagement-0.0.1-SNAPSHOT.jar" ]]; then
  export JAVA_HOME="${JAVA_HOME:-/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home}"
  export PATH="$JAVA_HOME/bin:$PATH"
  mvn -DskipTests package
fi

UI_ROOT="$(cd "$ROOT/../laundryui-ng17" && pwd)"
if [[ ! -d "$UI_ROOT/dist/LaundryUI" ]]; then
  export PATH="${PATH}:/Users/santhosh/Downloads/upgrade/node18/bin"
  (cd "$UI_ROOT" && NG_CLI_ANALYTICS=false ./node_modules/.bin/ng build --configuration production)
fi

echo "Provisioning droplet..."
PROVISION_OUT="$(./deploy/provision-do.sh)"
echo "$PROVISION_OUT"
export LAUNDRY_IP="$(echo "$PROVISION_OUT" | awk -F= '/^LAUNDRY_IP=/{print $2}')"
export LAUNDRY_HOST="$(echo "$PROVISION_OUT" | awk -F= '/^LAUNDRY_HOST=/{print $2}')"
if [[ -z "$LAUNDRY_IP" || -z "$LAUNDRY_HOST" ]]; then
  echo "Could not parse droplet IP/host from provision output" >&2
  exit 1
fi

echo "Waiting for SSH on $LAUNDRY_IP..."
for i in $(seq 1 60); do
  if ssh -o StrictHostKeyChecking=accept-new -o ConnectTimeout=5 -o BatchMode=yes root@"$LAUNDRY_IP" true 2>/dev/null; then
    break
  fi
  sleep 5
  if [[ "$i" -eq 60 ]]; then
    echo "SSH never became ready" >&2
    exit 1
  fi
done

echo "Bootstrapping server..."
scp -o StrictHostKeyChecking=accept-new \
  deploy/setup-server.sh deploy/Caddyfile deploy/laundry-api.service \
  root@"$LAUNDRY_IP":/tmp/
ssh root@"$LAUNDRY_IP" \
  "LAUNDRY_HOST='$LAUNDRY_HOST' LAUNDRY_ACME_EMAIL='$ACME_EMAIL' DB_PASSWORD='$DB_PASSWORD' AUTH_SECRET='$AUTH_SECRET' bash /tmp/setup-server.sh"

echo "Deploying app + DB..."
ROOT_UI="$UI_ROOT" ./deploy/deploy-app.sh "$DUMP"

echo
echo "Live URL: https://$LAUNDRY_HOST"
echo "DB password and auth secret are on the server in /opt/laundry/laundry.env"
