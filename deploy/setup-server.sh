#!/usr/bin/env bash
# Idempotent bootstrap for Ubuntu 24.04 on DigitalOcean (Bangalore).
# Run as root after the droplet is up: bash setup-server.sh
set -euo pipefail

export DEBIAN_FRONTEND=noninteractive
LAUNDRY_HOST="${LAUNDRY_HOST:-}"
LAUNDRY_ACME_EMAIL="${LAUNDRY_ACME_EMAIL:-admin@laundry.local}"
DB_PASSWORD="${DB_PASSWORD:-}"
AUTH_SECRET="${AUTH_SECRET:-}"

if [[ -z "$LAUNDRY_HOST" || -z "$DB_PASSWORD" || -z "$AUTH_SECRET" ]]; then
  echo "Usage: LAUNDRY_HOST=x.x.x.x.sslip.io DB_PASSWORD=... AUTH_SECRET=... bash setup-server.sh" >&2
  exit 1
fi

apt-get update -y
apt-get install -y ca-certificates curl gnupg mysql-server ufw

# Java 17
if ! command -v java >/dev/null 2>&1; then
  apt-get install -y openjdk-17-jre-headless
fi

# Caddy (auto HTTPS)
if ! command -v caddy >/dev/null 2>&1; then
  curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/gpg.key' | gpg --dearmor -o /usr/share/keyrings/caddy-stable-archive-keyring.gpg
  curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/debian.deb.txt' | tee /etc/apt/sources.list.d/caddy-stable.list
  apt-get update -y
  apt-get install -y caddy
fi

id laundry >/dev/null 2>&1 || useradd --system --home /opt/laundry --shell /usr/sbin/nologin laundry
mkdir -p /opt/laundry/uploads /var/www/laundryui /opt/laundry/backups
chown -R laundry:laundry /opt/laundry
chown -R caddy:caddy /var/www/laundryui || chown -R www-data:www-data /var/www/laundryui

# MySQL database + app user (bind local only)
systemctl enable --now mysql
mysql -uroot <<SQL
CREATE DATABASE IF NOT EXISTS laundryservice CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'laundry'@'localhost' IDENTIFIED BY '${DB_PASSWORD}';
ALTER USER 'laundry'@'localhost' IDENTIFIED BY '${DB_PASSWORD}';
GRANT ALL PRIVILEGES ON laundryservice.* TO 'laundry'@'localhost';
FLUSH PRIVILEGES;
SQL

cat > /opt/laundry/laundry.env <<EOF
SERVER_PORT=9090
SPRING_DATASOURCE_URL=jdbc:mysql://127.0.0.1:3306/laundryservice?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useLegacyDatetimeCode=false
SPRING_DATASOURCE_USERNAME=laundry
SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
LAUNDRY_AUTH_SECRET=${AUTH_SECRET}
LAUNDRY_CORS_ORIGINS=http://localhost:4200
FILE_UPLOAD_DIR=/opt/laundry/uploads/
EOF
chmod 640 /opt/laundry/laundry.env
chown root:laundry /opt/laundry/laundry.env

install -m 644 "$(dirname "$0")/laundry-api.service" /etc/systemd/system/laundry-api.service
systemctl daemon-reload

# Caddy site
install -m 644 "$(dirname "$0")/Caddyfile" /etc/caddy/Caddyfile
sed -i "s|{\\\$LAUNDRY_HOST}|${LAUNDRY_HOST}|g" /etc/caddy/Caddyfile || true
# Prefer env-based Caddyfile placeholders:
cat > /etc/caddy/Caddyfile <<EOF
{
	email ${LAUNDRY_ACME_EMAIL}
}

${LAUNDRY_HOST} {
	encode gzip

	handle_path /api/* {
		reverse_proxy 127.0.0.1:9090
	}

	handle {
		root * /var/www/laundryui
		try_files {path} /index.html
		file_server
	}
}
EOF

ufw allow OpenSSH
ufw allow 80/tcp
ufw allow 443/tcp
ufw --force enable

systemctl enable --now caddy
systemctl enable laundry-api

echo "Server base ready for ${LAUNDRY_HOST}."
echo "Next: copy laundry-api.jar, UI dist, and DB dump, then systemctl start laundry-api"
