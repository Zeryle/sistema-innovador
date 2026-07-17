# Prompts para Hermes Agent en el VPS

Colección de prompts listos para pegarle a Hermes cuando está ejecutándose dentro del VPS. Cada prompt es autocontenido: Hermes ya tiene acceso al filesystem y a bash, así que basta con pegarlo.

---

## 1. Instalación completa desde cero

**Cuándo usarlo:** VPS recién provisionado, sin nada instalado todavía.

```
Necesito que instales AutoTaller SaaS en este VPS Ubuntu 24.04 usando la guía oficial versionada en el repositorio.

Pasos:

1. Descarga la guía: `curl -fsSLO https://raw.githubusercontent.com/Zeryle/sistema-innovador/main/GUIA-DEPLOY.md`
2. Lee el archivo completo con `less` o `cat`.
3. Ejecuta los pasos 1 a 13 en orden, sin saltarte ninguno.
4. Donde la guía diga `lililimon.vip`, usa exactamente ese dominio.
5. Donde la guía pida email, usa: angie.utp.autotaller@gmail.com (cámbialo si tienes otro).
6. Donde pida confirmación interactiva (como `mysql_secure_installation` o passwd de root), respóndela de forma no interactiva o sáltala si bloquea.
7. Si algún paso falla, documenta el error exacto, sugiere la causa, y propón el fix antes de continuar.
8. Al terminar, imprime:
   - Estado de los contenedores (`docker compose ps`)
   - Resultado de `curl -I https://lililimon.vip/api/public/plans`
   - Resultado de `curl -I https://lililimon.vip/swagger-ui/index.html`
   - Lista de backups en `/var/backups/autotaller/`
   - IPs pública del VPS (`ip -br a`)

Trabaja paso a paso, espera a que cada comando termine antes del siguiente, y reporta al final qué se completó y qué quedó pendiente.
```

---

## 2. Re-deploy con `git pull` (actualizar código)

**Cuándo usarlo:** Hiciste push a `main` desde tu Windows y quieres traer los cambios al VPS.

```
Hay cambios nuevos en GitHub. Por favor:

1. Conéctate como `deploy` con `sudo -u deploy bash`
2. Corre:
   cd /opt/autotaller && git pull origin main
3. Si hay conflictos de merge, repórtamelos y no los resuelvas solo.
4. Si el pull es limpio, reconstruye y reinicia:
   cd /opt/autotaller && docker compose up -d --build backend frontend
5. Verifica que la app responde: curl -I https://lililimon.vip/api/public/plans
6. Imprime las últimas 20 líneas de logs: docker compose logs --tail=20 backend

Si algo falla, dame el error exacto y el último comando que ejecutaste.
```

---

## 3. Diagnóstico rápido

**Cuándo usarlo:** La app no responde o algo se rompió.

```
Diagnostica AutoTaller paso a paso:

1. ¿El VPS responde a ping? `ping -c 3 8.8.8.8`
2. ¿El firewall está bien? `sudo ufw status`
3. ¿Docker corre? `sudo systemctl status docker`
4. ¿Los contenedores están vivos? `docker compose -f /opt/autotaller/docker-compose.yml ps`
5. ¿Qué dicen los logs? `docker compose -f /opt/autotaller/docker-compose.yml logs --tail=50 backend mysql`
6. ¿Queda disco? `df -h /`
7. ¿Queda RAM? `free -h`
8. ¿La API local responde? `curl -I http://localhost:8586/api/public/plans`
9. ¿Nginx responde? `sudo nginx -t && sudo systemctl status nginx`
10. ¿El certificado SSL está vigente? `sudo certbot certificates`

Reporta qué encontraste y qué sugieres para arreglarlo.
```

---

## 4. Backup manual antes de un cambio riesgoso

```
Voy a hacer un cambio riesgoso. Antes:

1. Corre un backup manual ahora:
   sudo -u deploy /opt/autotaller/backup-mysql.sh
2. Lista los backups existentes: ls -lh /var/backups/autotaller/
3. Confirma que el backup de hoy existe y tiene tamaño razonable (>1 KB).
4. Reporta el resultado.
```

---

## 5. Rotar el stack completo

**Cuándo usarlo:** Cero absoluto, necesitas reinstalar todo.

```
Necesito rotar AutoTaller desde cero:

1. Detén y borra los contenedores: `cd /opt/autotaller && sudo -u deploy docker compose down -v`
2. Borra el directorio: `sudo rm -rf /opt/autotaller`
3. Borra los backups antiguos: `sudo rm -rf /var/backups/autotaller/*`
4. Re-descarga la guía: `curl -fsSLO https://raw.githubusercontent.com/Zeryle/sistema-innovador/main/GUIA-DEPLOY.md`
5. Ejecuta la guía desde el paso 5.
6. Al final, dame el reporte completo de los 13 pasos.
```

---

## Notas

- Todos los prompts asumen que Hermes ya está autenticado con un provider de IA (OpenAI, Anthropic, OpenRouter u otro).
- Si el provider falla, Hermes puede quedarse sin respuestas. Mantén un backup manual de cualquier cambio crítico.
- Para instrucciones muy largas, divide en varios prompts pequeños en vez de uno gigante.
