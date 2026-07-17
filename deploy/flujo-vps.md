# Flujo de trabajo entre Windows, GitHub y el VPS

Cómo se mantiene sincronizado el sistema: tu Windows es donde editas, GitHub es el origen de la verdad, el VPS ejecuta lo que está en `main`.

---

## Diagrama de flujo

```
┌──────────────┐    git push    ┌────────────┐   pull/run   ┌──────────────┐
│   Windows    │ ──────────────▶│   GitHub   │◀─────────────│  VPS OVH     │
│ (editas IDE) │                │  (main)    │              │  (Hermes)    │
└──────────────┘                └────────────┘              └──────────────┘
      ▲                                │                          │
      │                                │                          │
      └─────── pull (opcional) ────────┘                          │
                                                               comandos
                                                              bash, docker,
                                                              nginx, certbot
```

---

## Flujo 1 — Editar la guía y desplegar

1. En tu Windows, edita `GUIA-DEPLOY.md` (o `deploy/prompts-hermes.md`).
2. Commit:
   ```bash
   cd sistema-innovador
   git add GUIA-DEPLOY.md deploy/prompts-hermes.md
   git commit -m "docs: actualizar guía de deploy"
   git push origin main
   ```
3. En el VPS, como `deploy`:
   ```bash
   cd /opt/autotaller
   git pull origin main
   ```
4. (Opcional) Si quieres que Hermes use la guía nueva, pégale el prompt 1 de `deploy/prompts-hermes.md`.

---

## Flujo 2 — Re-deployar AutoTaller con código nuevo

1. En tu Windows, edita el código del backend o frontend.
2. Build local para verificar (opcional):
   ```bash
   cd sistema-innovador
   ./mvnw clean package -DskipTests
   ```
3. Commit y push:
   ```bash
   git add .
   git commit -m "feat: nueva funcionalidad"
   git push origin main
   ```
4. En el VPS, como `deploy`:
   ```bash
   cd /opt/autotaller
   git pull origin main
   docker compose up -d --build backend frontend
   docker compose logs --tail=40 backend
   ```
5. Verifica:
   ```bash
   curl -I https://lililimon.vip/api/public/plans
   ```

---

## Flujo 3 — Hermes ejecuta la guía de forma autónoma

1. Hermes ya está corriendo en el VPS (instalado con `curl -fsSL https://hermes-agent.nousresearch.com/install.sh | bash`).
2. Pégale el prompt 1 de `deploy/prompts-hermes.md` (sección "Instalación completa desde cero").
3. Hermes descarga `GUIA-DEPLOY.md` desde GitHub y la ejecuta paso a paso.
4. Al final, Hermes te da un reporte con el estado del stack.

Para que esto funcione, Hermes debe:

- Tener `~/.hermes/config.yaml` con un provider de IA (OpenRouter, Anthropic, etc.) y una API key válida.
- Poder ejecutar comandos bash con `sudo` (lo que ya está si instalaste Hermes como `ubuntu` o `deploy` con sudo NOPASSWD).

---

## Sincronización de la guía

`GUIA-DEPLOY.md` es la fuente de verdad. Cada vez que cambies algo en el deploy, actualiza también la guía. El flujo de revisión es:

1. Cambias código → funciona localmente.
2. Despliegas en el VPS con el flujo 2.
3. Si encontraste un paso que faltaba en la guía, lo agregas.
4. Commit + push de la guía.
5. La próxima vez que Hermes ejecute la guía, incluirá el cambio.

---

## Buenas prácticas

- **Nunca** edites archivos en `/opt/autotaller` directamente desde el VPS. Los cambios locales se pierden en el próximo `git pull`. Si necesitas ajustar algo, hazlo en tu Windows, haz commit, push, y pull desde el VPS.
- Si un comando falla, **no lo sigas intentando a ciegas**. Pega el error en el chat de Hermes con la sesión abierta y deja que diagnostique.
- Los backups en `/var/backups/autotaller/` son críticos. No los borres salvo que estés seguro.
- El certificado SSL se renueva solo. Si ves un email de Let's Encrypt, no lo ignores.
