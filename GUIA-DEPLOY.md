# Guía de Despliegue — AutoTaller SaaS en VPS Ubuntu 24.04

> **Autor:** Zeryle · **TF:** UTP Desarrollo Web Integrado S17 (julio 2026)
> **Stack:** Spring Boot 3.5 + Angular 19 + MySQL 8 sobre Ubuntu 24.04 LTS
> **Tiempo estimado:** 35-50 minutos en un VPS limpio

Esta guía está pensada para ser ejecutada por un agente (Hermes Agent) o un humano con SSH. Cada paso es idempotente: si algo falla, se puede reintentar.

---

## 0. Pre-requisitos

Antes de empezar confirma:

| Recurso | Mínimo | Recomendado | Verificar |
|---|---|---|---|
| OS | Ubuntu 24.04 LTS | Ubuntu 24.04 LTS | `lsb_release -a` |
| RAM | 2 GB | 4 GB | `free -h` |
| Disco | 10 GB | 25 GB | `df -h /` |
| vCPU | 1 | 2 | `nproc` |
| IPv4 pública fija | sí | sí | `ip -br a` |
| Dominio apuntando a la IP | sí | sí | `dig +short $DOMAIN A` |
| Acceso SSH con clave | sí | sí | funciona `ssh user@vps` |

Si falta RAM/disco, ejecuta el paso 1B (swap) antes de continuar.

---

## 1. Sistema base

### 1.1. Actualizar paquetes

```bash
set -e
sudo apt update
sudo apt upgrade -y
```

### 1.2. Herramientas mínimas

```bash
sudo apt install -y \
  curl wget git ufw software-properties-common \
  apt-transport-https ca-certificates gnupg lsb-release \
  nano htop jq unzip
```

### 1.3. Swap de 2 GB (opcional pero recomendado para VPS de 2 GB)

```bash
if [ ! -f /swapfile ]; then
  sudo fallocate -l 2G /swapfile
  sudo chmod 600 /swapfile
  sudo mkswap /swapfile
  sudo swapon /swapfile
  echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab > /dev/null
fi
free -h
```

### 1.4. Zona horaria

```bash
sudo timedatectl set-timezone America/Lima
```

---

## 2. Stack de aplicación

### 2.1. OpenJDK 21

```bash
sudo apt install -y openjdk-21-jdk
java -version
```

### 2.2. Docker + Docker Compose

```bash
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg \
  | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
  https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" \
  | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io \
  docker-buildx-plugin docker-compose-plugin
docker --version
docker compose version
```

### 2.3. MySQL nativo como fallback

> Lo ideal es usar el MySQL del `docker-compose.yml`. Si prefieres nativo:

```bash
sudo apt install -y mysql-server
sudo systemctl enable --now mysql
sudo mysql_secure_installation
```

### 2.4. Nginx + Certbot

```bash
sudo apt install -y nginx certbot python3-certbot-nginx
sudo systemctl enable --now nginx
```

---

## 3. Firewall

```bash
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow OpenSSH
sudo ufw allow 'Nginx Full'
sudo ufw --force enable
sudo ufw status verbose
```

---

## 4. Usuario `deploy` (no-root para la app)

```bash
if ! id deploy >/dev/null 2>&1; then
  sudo adduser --disabled-password --gecos "Deploy User" deploy
fi
sudo usermod -aG docker deploy
echo "deploy ALL=(ALL) NOPASSWD:ALL" | sudo tee /etc/sudoers.d/deploy
sudo chmod 440 /etc/sudoers.d/deploy
```

---

## 5. Clonar el repositorio

```bash
sudo mkdir -p /opt
sudo chown deploy:deploy /opt
sudo -u deploy git clone https://github.com/Zeryle/sistema-innovador.git /opt/autotaller
sudo -u deploy bash -c 'cd /opt/autotaller && git config --global --add safe.directory /opt/autotaller'
ls -la /opt/autotaller
```

Verifica que existan: `pom.xml`, `Dockerfile`, `docker-compose.yml`, `frontend/`, `src/`.

Si el repositorio es privado, primero configura un PAT de GitHub en `~deploy/.netrc` o usa SSH keys.

---

## 6. Configurar MySQL con Docker

### 6.1. Apagar MySQL nativo si está corriendo (para liberar el 3306)

```bash
if systemctl is-active --quiet mysql; then
  sudo systemctl stop mysql
  sudo systemctl disable mysql
fi
sudo ss -tlnp | grep 3306 || echo "puerto 3306 libre"
```

### 6.2. Crear base de datos y usuario

```bash
sudo -u deploy bash <<'EOF'
cd /opt/autotaller
docker compose up -d mysql
docker compose logs -f mysql &
LOGS_PID=$!
for i in $(seq 1 60); do
  if docker exec autotaller-mysql mysqladmin ping -uroot -proot -hlocalhost >/dev/null 2>&1; then
    break
  fi
  sleep 2
done
kill $LOGS_PID 2>/dev/null || true
docker exec autotaller-mysql mysql -uroot -proot -e "SHOW DATABASES;"
EOF
```

> Si `docker exec` falla con "container not found", espera 10 segundos y reintenta.

---

## 7. Levantar backend + frontend

```bash
sudo -u deploy bash <<'EOF'
cd /opt/autotaller
docker compose up -d --build backend frontend
docker compose ps
docker compose logs --tail=80 backend
EOF
```

Espera a ver en los logs:

```
Started SistemaInnovadorApplication in X.XXX seconds
```

Verifica que la API responde:

```bash
curl -s http://localhost:8586/api/public/plans | head -c 400
```

Si devuelve JSON con la lista de planes, todo va bien.

---

## 8. Nginx como reverse proxy

### 8.1. Configuración

Reemplaza `lililimon.vip` por tu dominio real:

```bash
DOMAIN="lililimon.vip"

sudo tee /etc/nginx/sites-available/autotaller > /dev/null <<EOF
server {
    listen 80;
    server_name ${DOMAIN} www.${DOMAIN};

    location /.well-known/acme-challenge/ {
        root /var/www/html;
    }

    location / {
        proxy_pass http://127.0.0.1:8586;
        proxy_http_version 1.1;
        proxy_set_header Host              \$host;
        proxy_set_header X-Real-IP         \$remote_addr;
        proxy_set_header X-Forwarded-For   \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        client_max_body_size 25M;
    }
}
EOF

sudo ln -sf /etc/nginx/sites-available/autotaller /etc/nginx/sites-enabled/autotaller
sudo rm -f /etc/nginx/sites-enabled/default
sudo nginx -t
sudo systemctl reload nginx
```

### 8.2. Verificar HTTP antes de HTTPS

```bash
curl -I http://${DOMAIN}
```

Debe responder 200 o 304.

---

## 9. Apuntar el dominio al VPS (en Porkbun o tu registrador)

Crea estos registros en el panel DNS:

| Tipo | Host | Respuesta | TTL |
|------|------|-----------|-----|
| A | (vacío = `@`) | tu IPv4 pública | 600 |
| A | www | tu IPv4 pública | 600 |
| AAAA | (vacío = `@`) | tu IPv6 pública | 600 |
| AAAA | www | tu IPv6 pública | 600 |

Verifica la propagación:

```bash
dig +short ${DOMAIN} A @8.8.8.8
dig +short ${DOMAIN} AAAA @8.8.8.8
```

Si no propaga, espera 5-30 minutos y vuelve a intentar.

---

## 10. HTTPS con Let's Encrypt

```bash
EMAIL="tu_email_real@dominio.com"
DOMAIN="lililimon.vip"

sudo certbot --nginx \
  -d ${DOMAIN} -d www.${DOMAIN} \
  --non-interactive --agree-tos \
  -m ${EMAIL}
```

Certbot renovará el certificado automáticamente. Verifica:

```bash
sudo systemctl status certbot.timer
sudo certbot certificates
```

Verifica HTTPS:

```bash
curl -I https://${DOMAIN}
curl -I https://${DOMAIN}/api/public/plans
curl -I https://${DOMAIN}/swagger-ui/index.html
```

Las tres deben responder HTTP/2 200.

---

## 11. Backups automáticos de MySQL

### 11.1. Script de backup

```bash
sudo -u deploy tee /opt/autotaller/backup-mysql.sh > /dev/null <<'EOF'
#!/usr/bin/env bash
set -euo pipefail
TS=$(date +%Y%m%d-%H%M%S)
FILE="/var/backups/autotaller/db-${TS}.sql.gz"
mkdir -p /var/backups/autotaller
docker exec autotaller-mysql mysqldump -uroot -proot autotaller | gzip > "$FILE"
echo "[$(date -Is)] backup written: $FILE ($(du -h "$FILE" | cut -f1))"
find /var/backups/autotaller -name "db-*.sql.gz" -mtime +14 -delete
EOF
sudo chmod +x /opt/autotaller/backup-mysql.sh
```

### 11.2. Cron diario a las 03:00

```bash
( sudo -u deploy crontab -l 2>/dev/null | grep -v backup-mysql.sh
  echo "0 3 * * * /opt/autotaller/backup-mysql.sh >> /var/log/autotaller-backup.log 2>&1"
) | sudo -u deploy crontab -
sudo -u deploy crontab -l
```

---

## 12. Hardening final (opcional pero recomendado)

### 12.1. fail2ban

```bash
sudo apt install -y fail2ban
sudo systemctl enable --now fail2ban
```

### 12.2. fail2ban para SSH

```bash
sudo tee /etc/fail2ban/jail.d/sshd.local > /dev/null <<'EOF'
[sshd]
enabled = true
port = ssh
filter = sshd
logpath = /var/log/auth.log
maxretry = 5
findtime = 600
bantime = 3600
EOF
sudo systemctl restart fail2ban
sudo fail2ban-client status sshd
```

### 12.3. Banner legal

```bash
sudo tee /etc/issue.net > /dev/null <<'EOF'
Authorized access only. All activity is logged.
EOF
```

---

## 13. Verificación final

```bash
echo "=== Estado del stack ==="
docker compose -f /opt/autotaller/docker-compose.yml ps
echo
echo "=== HTTPS health ==="
curl -I https://lililimon.vip/api/public/plans
curl -I https://lililimon.vip/swagger-ui/index.html
echo
echo "=== Backups ==="
ls -lh /var/backups/autotaller/ | tail -5
echo
echo "=== Firewall ==="
sudo ufw status
echo
echo "=== SSL ==="
sudo certbot certificates
```

URLs finales del proyecto:

- Frontend + API: `https://lililimon.vip`
- Swagger UI: `https://lililimon.vip/swagger-ui/index.html`
- OpenAPI JSON: `https://lililimon.vip/v3/api-docs`
- Health check: `https://lililimon.vip/api/public/plans`

---

## Troubleshooting

| Síntoma | Causa probable | Solución |
|---|---|---|
| `docker compose up -d mysql` falla con "address already in use" en :3306 | MySQL nativo tomó el puerto | `sudo systemctl stop mysql && sudo systemctl disable mysql` |
| `sshd -t` da "Bad configuration option" | Caracteres invisibles o líneas duplicadas | Reemplazar el bloque completo de directivas al final del archivo |
| Certbot dice "Could not validate domain" | DNS no propaga todavía | Esperar 30 min y reintentar; verificar con `dig +short dominio A @8.8.8.8` |
| Backend arranca pero 502 Bad Gateway | Nginx apunta a IP/puerto incorrecto | Verificar `proxy_pass http://127.0.0.1:8586;` y que el contenedor backend esté corriendo |
| Disco lleno | Backups sin rotación o logs de Docker | `docker system prune -a` y verificar `find /var/backups -mtime +14` |

---

## Re-deploy (actualizar la app)

Para actualizar el código y reiniciar la app:

```bash
sudo -u deploy bash <<'EOF'
cd /opt/autotaller
git pull origin main
docker compose up -d --build backend frontend
docker compose ps
docker compose logs --tail=40 backend
EOF
```

Verifica:

```bash
curl -I https://lililimon.vip/api/public/plans
```

---

## Hard reset (empezar de cero)

```bash
sudo -u deploy bash <<'EOF'
cd /opt/autotaller
docker compose down -v
docker system prune -a
EOF
sudo rm -rf /opt/autotaller
```

Luego vuelve a ejecutar esta guía desde el paso 5.

---

**Fin de la guía.** Para actualizar la guía, edita este archivo y haz push a `main` en GitHub.
