#!/usr/bin/env bash
# Create a Bangalore droplet and print the public IP + sslip.io host.
# Requires: doctl auth init (or DIGITALOCEAN_ACCESS_TOKEN), and an SSH key already on the account.
set -euo pipefail

NAME="${DROPLET_NAME:-laundry-blr}"
REGION="${DROPLET_REGION:-blr1}"
SIZE="${DROPLET_SIZE:-s-2vcpu-2gb}"
IMAGE="${DROPLET_IMAGE:-ubuntu-24-04-x64}"

if [[ -n "${DIGITALOCEAN_ACCESS_TOKEN:-}" ]]; then
  doctl auth init --access-token "$DIGITALOCEAN_ACCESS_TOKEN" >/dev/null
fi

if ! doctl account get >/dev/null 2>&1; then
  echo "DigitalOcean is not authenticated. Run: doctl auth init" >&2
  echo "Or export DIGITALOCEAN_ACCESS_TOKEN=dop_v1_..." >&2
  exit 1
fi

SSH_KEYS="$(doctl compute ssh-key list --format ID --no-header | tr '\n' ',' | sed 's/,$//')"
if [[ -z "$SSH_KEYS" ]]; then
  PUB="${SSH_PUBKEY_FILE:-$HOME/.ssh/id_ed25519.pub}"
  if [[ ! -f "$PUB" ]]; then
    echo "No SSH keys on the DO account and no $PUB locally." >&2
    exit 1
  fi
  doctl compute ssh-key create laundry-mac --public-key-file "$PUB" >/dev/null
  SSH_KEYS="$(doctl compute ssh-key list --format ID --no-header | tr '\n' ',' | sed 's/,$//')"
fi

EXISTING_IP="$(doctl compute droplet list --format Name,PublicIPv4 --no-header | awk -v n="$NAME" '$1==n {print $2}')"
if [[ -n "$EXISTING_IP" ]]; then
  echo "Droplet $NAME already exists at $EXISTING_IP"
  echo "LAUNDRY_HOST=${EXISTING_IP}.sslip.io"
  echo "LAUNDRY_IP=${EXISTING_IP}"
  exit 0
fi

doctl compute droplet create "$NAME" \
  --region "$REGION" \
  --size "$SIZE" \
  --image "$IMAGE" \
  --ssh-keys "$SSH_KEYS" \
  --enable-monitoring \
  --wait

IP="$(doctl compute droplet list --format Name,PublicIPv4 --no-header | awk -v n="$NAME" '$1==n {print $2}')"
echo "Created $NAME"
echo "LAUNDRY_IP=${IP}"
echo "LAUNDRY_HOST=${IP}.sslip.io"
