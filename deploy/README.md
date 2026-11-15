# Cloud deploy (DigitalOcean Bangalore)

One droplet runs MySQL, the Spring Boot API, and Caddy (Angular UI + HTTPS).

## Prerequisites

1. DigitalOcean account and API token (`dop_v1_...`)
2. Local builds:
   - API: `mvn -DskipTests package`
   - UI: `ng build --configuration production` in `laundryui-ng17`

## One-shot publish

```bash
# Save token (gitignored)
echo 'dop_v1_...' > deploy/.do_token

export DIGITALOCEAN_ACCESS_TOKEN="$(tr -d ' \n' < deploy/.do_token)"
export DB_PASSWORD="$(openssl rand -hex 16)"
export AUTH_SECRET="$(openssl rand -hex 32)"

# 1) Create droplet in blr1
eval "$(./deploy/provision-do.sh | tee /tmp/laundry-do.txt | sed -n 's/^LAUNDRY_/export LAUNDRY_/p')"

# 2) Wait for SSH, bootstrap server
for i in $(seq 1 30); do ssh -o StrictHostKeyChecking=accept-new -o ConnectTimeout=5 root@"$LAUNDRY_IP" true && break; sleep 10; done
scp deploy/setup-server.sh deploy/Caddyfile deploy/laundry-api.service root@"$LAUNDRY_IP":/tmp/
ssh root@"$LAUNDRY_IP" "LAUNDRY_HOST=$LAUNDRY_HOST DB_PASSWORD=$DB_PASSWORD AUTH_SECRET=$AUTH_SECRET bash /tmp/setup-server.sh"

# 3) Ship app + restore local DB
./deploy/deploy-app.sh deploy/laundryservice.sql.gz
```

Open `https://$LAUNDRY_HOST` (uses Let’s Encrypt via sslip.io).

## Local overrides already supported

`application.properties` reads:

- `SPRING_DATASOURCE_URL` / `USERNAME` / `PASSWORD`
- `LAUNDRY_AUTH_SECRET`
- `LAUNDRY_CORS_ORIGINS`
- `FILE_UPLOAD_DIR`
- `SERVER_PORT`
