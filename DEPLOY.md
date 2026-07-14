# Guía de despliegue — AutoTaller SaaS

Esta guía cubre cómo desplegar el backend Spring Boot y el frontend
Angular en varias plataformas. Elige la que mejor se ajuste a tu caso.

> **TL;DR para la rúbrica del TF:** la opción más rápida es **Render**,
> todo desde GitHub, sin tocar la consola. Ver [§ Render](#render).

---

## Índice

1. [Requisitos previos](#requisitos-previos)
2. [Variables de entorno](#variables-de-entorno)
3. [Render](#render)
4. [Railway](#railway)
5. [Fly.io](#flyio)
6. [VPS / servidor propio con Docker Compose](#vps--servidor-propio-con-docker-compose)
7. [Despliegue desde Docker Hub / GHCR](#despliegue-desde-docker-hub--ghcr)
8. [Post-deploy checklist](#post-deploy-checklist)
9. [Troubleshooting](#troubleshooting)

---

## Requisitos previos

- Una cuenta de GitHub con el repo
  [`Zeryle/sistema-innovador`](https://github.com/Zeryle/sistema-innovador)
  público o con permisos para la plataforma de deploy.
- Si vas a usar Docker: Docker Desktop local para probar antes de subir.
- Si vas a Render o Railway: una cuenta (gratis es suficiente para
  demos).

## Variables de entorno

El backend Spring Boot lee estas variables (con defaults razonables):

| Variable                       | Default                                  | Descripción                              |
| ------------------------------ | ---------------------------------------- | ---------------------------------------- |
| `SPRING_PROFILES_ACTIVE`       | `dev`                                    | `dev` (H2) o `mysql` (producción)        |
| `SERVER_PORT`                  | `8586`                                   | Puerto HTTP                              |
| `SPRING_DATASOURCE_URL`        | `jdbc:h2:mem:autotaller`                 | JDBC URL completa                        |
| `SPRING_DATASOURCE_USERNAME`   | `sa`                                     | Usuario BD                               |
| `SPRING_DATASOURCE_PASSWORD`   | (vacío)                                  | Password BD                              |
| `WHATSAPP_MODE`                | `mock`                                   | `mock` o `live`                          |
| `WHATSAPP_TOKEN`               | (vacío)                                  | Token Meta (solo si `live`)              |
| `WHATSAPP_PHONE_ID`            | (vacío)                                  | Phone number ID de Meta                  |
| `WHATSAPP_BUSINESS_ID`         | (vacío)                                  | Business Account ID                      |
| `WHATSAPP_VERIFY_TOKEN`        | `auto-taller-demo-verify`                | Webhook verify token                     |
| `JAVA_OPTS`                    | `-XX:+UseG1GC -XX:MaxRAMPercentage=75.0` | Tuning de JVM                            |

---

## Render

**Por qué Render:** free tier para servicios web, deploys automáticos
desde GitHub, base de datos PostgreSQL gratis (pero aquí usamos MySQL —
plan Starter de $7/mes, o usa el free de [JawsDB](https://jawsdb.com) /
[PlanetScale](https://planetscale.com)).

### 1. Crear MySQL

1. Ve a [render.com](https://render.com) → **New +** → **PostgreSQL**
   *o* **Key Value Store** (Render no da MySQL nativo en free).
2. **Alternativa gratis:** usa **Railway** solo para MySQL y **Render**
   para el backend. Más simple: ve a [Railway](#railway) y haz todo allí.

### 2. Backend (Web Service)

1. **New +** → **Web Service** → conecta tu repo de GitHub.
2. Configuración:
   - **Name**: `autotaller-backend`
   - **Root Directory**: *(vacío)*
   - **Runtime**: `Docker`
   - **Dockerfile Path**: `./Dockerfile`
   - **Plan**: Free
3. **Environment**:
   ```
   SPRING_PROFILES_ACTIVE=mysql
   SPRING_DATASOURCE_URL=jdbc:mysql://<host>:<port>/autotaller?...
   SPRING_DATASOURCE_USERNAME=...
   SPRING_DATASOURCE_PASSWORD=...
   WHATSAPP_MODE=mock
   ```
4. **Health Check Path**: `/api/public/plans`
5. Click **Create Web Service**. Render construye la imagen y la
   expone en `https://autotaller-backend.onrender.com`.

### 3. Frontend (Static Site)

1. **New +** → **Static Site** → conecta el repo.
2. **Root Directory**: `frontend`
3. **Build Command**: `corepack enable && corepack prepare pnpm@latest --activate && pnpm install --frozen-lockfile && pnpm build`
4. **Publish Directory**: `dist/browser`
5. **Rewrite Rules** (para SPA fallback):

   ```
   source: /api/*
   destination: https://autotaller-backend.onrender.com/api/*
   action: Rewrite

   source: /*
   destination: /index.html
   action: Rewrite
   ```

6. Click **Create Static Site**.

### 4. Swagger público

Una vez levantado el backend:

- `https://autotaller-backend.onrender.com/swagger-ui/index.html`
- `https://autotaller-backend.onrender.com/v3/api-docs`

Pega estas URLs en tu **Informe Final** y en el **Video Grupal**.

---

## Railway

**Por qué Railway:** MySQL nativo como add-on, deploy desde GitHub con
un click, free tier con créditos mensuales.

1. Ve a [railway.app](https://railway.app) → **Login with GitHub**.
2. **New Project** → **Deploy from GitHub repo**.
3. Selecciona `Zeryle/sistema-innovador`.
4. Railway detecta el Dockerfile automáticamente.
5. Click en el servicio → **Variables**:

   ```
   SPRING_PROFILES_ACTIVE=mysql
   WHATSAPP_MODE=mock
   ```

6. **Add MySQL** desde el panel: click **+ New** → **Database** →
   **MySQL**. Railway inyecta `MYSQL_URL` y compañía automáticamente.
   Necesitas mapear a `SPRING_DATASOURCE_URL` — usa la sección
   **Variables** → **Reference Variable**:

   ```
   SPRING_DATASOURCE_URL=${{MySQL.MYSQL_URL}}
   SPRING_DATASOURCE_USERNAME=${{MySQL.MYSQL_USER}}
   SPRING_DATASOURCE_PASSWORD=${{MySQL.MYSQL_PASSWORD}}
   ```

   Nota: adapta el `MYSQL_URL` para incluir `?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`.

7. **Settings** → **Generate Domain**. Anota la URL.
8. **Deploy**. Railway hace push automático en cada commit a `main`.

### Frontend

Repite con un segundo servicio apuntando a `frontend/Dockerfile`, con
la variable `BACKEND_URL=https://<backend>.up.railway.app` (y úsala en
el build para inyectar la API base URL si quieres).

---

## Fly.io

**Por qué Fly.io:** free tier generoso, control total, ideal si quieres
mostrar Docker multi-stage y Cloud/DevOps en la rúbrica.

### 1. Instalar flyctl

```bash
# Windows (PowerShell)
iwr https://fly.io/install.ps1 -useb | iex

# macOS / Linux
curl -L https://fly.io/install.sh | sh
```

### 2. Login

```bash
fly auth signup   # o `fly auth login` si ya tienes cuenta
```

### 3. Lanzar MySQL

```bash
fly launch --image mysql:8 --name autotaller-db \
  --env MYSQL_ROOT_PASSWORD=root --env MYSQL_DATABASE=autotaller
```

O usa el add-on [Fly Postgres](https://fly.io/docs/postgres/) y traduce
la URL a MySQL… o más simple: usa un [Turso](https://turso.tech/) para
SQLite distribuido. Lo más limpio para MySQL real: **unmanaged en Fly**.

### 4. Backend

```bash
cd sistema-innovador
fly launch --no-deploy \
  --name autotaller-backend \
  --dockerfile Dockerfile \
  --internal-port 8586
```

Edita `fly.toml` que se generó:

```toml
[env]
  SPRING_PROFILES_ACTIVE = "mysql"
  SPRING_DATASOURCE_URL = "jdbc:mysql://<db-host>:3306/autotaller?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
  SPRING_DATASOURCE_USERNAME = "root"
  SPRING_DATASOURCE_PASSWORD = "root"
  WHATSAPP_MODE = "mock"

[[services]]
  internal_port = 8586
  protocol = "tcp"

  [[services.ports]]
    port = 80
    handlers = ["http"]
    force_https = true

  [[services.ports]]
    port = 443
    handlers = ["tls", "http"]

  [services.concurrency]
    type = "connections"
    hard_limit = 100
    soft_limit = 80

[[services.tcp_checks]]
  interval = "15s"
  timeout = "2s"
  grace_period = "30s"
```

```bash
fly deploy
fly open
```

### 5. Frontend

Repite para `frontend/Dockerfile`. El `nginx-spa.conf` ya incluye
proxy inverso a `http://backend:8586/api/`, así que si desplegarás ambos
en el mismo `fly.toml` (multi-process), aprovecha el networking interno
de Fly. Si van separados, actualiza la línea `proxy_pass` del nginx a
`http://autotaller-backend.flycast:8586`.

---

## VPS / servidor propio con Docker Compose

**Cuándo usarlo:** quieres control total, una sola máquina y los
certificados TLS via Traefik / Caddy.

### 1. Pre-requisitos en el servidor

```bash
# Ubuntu 22.04
sudo apt update && sudo apt install -y docker.io docker-compose-plugin
sudo usermod -aG docker $USER
```

### 2. Clonar el repo

```bash
git clone https://github.com/Zeryle/sistema-innovador.git
cd sistema-innovador
```

### 3. Levantar

```bash
docker compose up -d --build
docker compose logs -f backend
```

### 4. (Recomendado) Reverse proxy con Caddy

Para HTTPS automático con Let's Encrypt, instala Caddy y configura:

```caddyfile
# /etc/caddy/Caddyfile
api.tu-dominio.com {
    reverse_proxy localhost:8586
}

app.tu-dominio.com {
    reverse_proxy localhost:8080
}
```

```bash
sudo systemctl reload caddy
```

---

## Despliegue desde Docker Hub / GHCR

Si ya subiste las imágenes (CI las publica en tags `v*`):

```bash
docker pull ghcr.io/zeryle/autotaller-backend:latest
docker pull ghcr.io/zeryle/autotaller-frontend:latest

docker run -d --name autotaller-backend \
  -p 8586:8586 \
  -e SPRING_PROFILES_ACTIVE=mysql \
  -e SPRING_DATASOURCE_URL='jdbc:mysql://host.docker.internal:3306/autotaller?...' \
  ghcr.io/zeryle/autotaller-backend:latest

docker run -d --name autotaller-frontend \
  -p 8080:80 \
  ghcr.io/zeryle/autotaller-frontend:latest
```

---

## Post-deploy checklist

Antes de grabar el video grupal, verifica que todo funcione:

- [ ] `GET https://<backend>/api/public/plans` → 200 con 3 planes
- [ ] `GET https://<backend>/swagger-ui/index.html` → 200 (Swagger UI)
- [ ] `GET https://<backend>/v3/api-docs` → 200 (spec JSON)
- [ ] `GET https://<frontend>/` → 200 (Angular carga)
- [ ] `POST https://<backend>/api/auth/register` → 201 con usuario
- [ ] `POST https://<backend>/api/auth/login` → 200 con JWT
- [ ] Frontend hace login y muestra el dashboard (con KPI charts)
- [ ] Descargar Excel desde `/api/analytics/export/xlsx` → xlsx válido

## Troubleshooting

### El backend no arranca: `Communications link failure`

MySQL no accesible. Verifica:
- `SPRING_DATASOURCE_URL` apunta al host correcto
- El puerto está abierto en el firewall / security group
- El usuario tiene permisos

### Swagger UI carga pero `/v3/api-docs` da 500

Springdoc mal configurado. Verifica versión:
```xml
<version>2.8.6</version>   <!-- compatible con Spring Boot 3.5 -->
```

### Frontend muestra pantalla en blanco

- `index.html` no se está sirviendo — verifica `nginx-spa.conf` tiene
  `try_files $uri $uri/ /index.html;`
- 404 en rutas como `/app/dashboard` — el rewrite de Angular falla. Si
  usas Render Static Site, asegúrate de tener la regla de rewrite.

### JWT expirado

Tokens JWT tienen TTL por defecto corto. Para sesiones largas, usa el
endpoint `POST /api/auth/refresh` con el refresh token.