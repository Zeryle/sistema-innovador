# AutoTaller SaaS

> Sistema de gestión para talleres automotrices — SaaS multi-tenant con
> autenticación JWT, catálogo de partes, órdenes de trabajo, clientes,
> vehículos, recordatorios, analítica, facturación y notificaciones
> WhatsApp. Construido como Trabajo Final del curso **Desarrollo Web
> Integrado (DWI)** — Universidad Tecnológica del Perú (UTP).

[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://adoptium.net/)
[![Spring Boot 3.5](https://img.shields.io/badge/Spring%20Boot-3.5.6-green.svg)](https://spring.io/projects/spring-boot)
[![Angular 19](https://img.shields.io/badge/Angular-19-DD0031.svg)](https://angular.dev/)
[![DDD](https://img.shields.io/badge/architecture-DDD-blue.svg)](#arquitectura)
[![License](https://img.shields.io/badge/license-Academic-lightgrey.svg)](#licencia)

---

## Tabla de contenidos

1. [Características](#características)
2. [Stack tecnológico](#stack-tecnológico)
3. [Arquitectura](#arquitectura)
4. [Bounded contexts](#bounded-contexts)
5. [Inicio rápido](#inicio-rápido)
6. [Documentación de la API](#documentación-de-la-api)
7. [Tests](#tests)
8. [Despliegue](#despliegue)
9. [CI/CD](#cicd)
10. [Estructura del repo](#estructura-del-repo)
11. [Licencia](#licencia)

---

## Características

- **Multi-tenant** — Cada taller (tenant) tiene sus propios clientes,
  vehículos, órdenes y administradores, aislados lógicamente en base de
  datos.
- **Autenticación JWT** — Login, refresh tokens, registro y endpoint
  `GET /api/auth/me` con filtro personalizado `JwtAuthenticationFilter`.
- **Órdenes de trabajo (Work Orders)** — Ciclo de vida completo: crear,
  actualizar estado, asignar, finalizar.
- **Clientes y vehículos** — CRUD con validaciones de dominio.
- **Recordatorios** — Programación de notificaciones automáticas.
- **Analítica avanzada** — KPIs, top-customers, tendencia mensual,
  comparación por periodo, breakdown por servicio, exportación a Excel
  vía Apache POI.
- **Facturación con Stripe (mock)** — Planes FREE / BASIC / PREMIUM,
  checkout end-to-end con webhook simulado.
- **WhatsApp Cloud API (mock)** — Plantillas, respuestas automáticas,
  inbox/outbox. Stub para MetaCloudApiAdapter (live mode pendiente de
  habilitar).
- **Catálogo de partes** — Categorías y compatibilidades por modelo.
- **Documentación OpenAPI 3.1** — Swagger UI accesible y spec JSON.

## Stack tecnológico

| Capa            | Tecnología                                  |
| --------------- | ------------------------------------------- |
| Backend         | Java 17 · Spring Boot 3.5.6 · Spring Security · Spring Data JPA |
| Persistencia    | MySQL 8 (prod) · H2 in-memory (dev/test)    |
| Auth            | JWT (jjwt 0.12.6) + BCrypt                  |
| Build           | Maven 3.9 + Wrapper                         |
| Frontend        | Angular 19 · TypeScript 5.6 · Tailwind 3.4 · Chart.js · ng2-charts |
| Build frontend  | pnpm 9 + Angular CLI                        |
| Pagos           | Stripe SDK (mock gateway, swap a live)      |
| Mensajería      | WhatsApp Cloud API (mock, swap a MetaCloudApi) |
| Excel           | Apache POI 5.3                              |
| Docs API        | springdoc-openapi 2.8.6 (Swagger UI)        |
| Container       | Docker (multi-stage JDK 17 → JRE 17)        |
| Orquestación    | docker-compose (MySQL + backend + frontend) |
| CI/CD           | GitHub Actions (build, test, docker push)   |

## Arquitectura

El proyecto sigue **Domain-Driven Design (DDD)** con bounded contexts
claramente delimitados. Cada BC es un módulo Maven interno bajo
`com.utp.myapp.<context>` con cuatro capas estándar:

```
<context>/
├── domain/         # Aggregates, Value Objects, Domain Events, Repository interfaces
├── application/    # Commands, Queries, Handlers (CQRS), DTOs, Assemblers
├── infraestructure/ # JPA entities + mappers + adapters, security, gateways
└── interfaces/     # REST controllers, webhook controllers
```

### Patrones aplicados

- **CQRS** — Commands (`*Command`) y Queries (`*Query`) separados, cada
  uno con su `*Handler`. Lecturas optimizadas en `analytics` y `sales`.
- **Repository Pattern** — `domain/model/repository/I*Repository` (puertos)
  implementados por `infraestructure/persistence/jpa/adapters/`
  (adaptadores).
- **Hexagonal / Ports & Adapters** — La capa de dominio no conoce JPA,
  ni HTTP, ni Stripe. Los detalles se inyectan vía interfaces.
- **Adapter Pattern** — `MockStripeGateway` ↔ `PaymentGateway`,
  `MockWhatsAppGateway` ↔ `WhatsAppGateway`, `MetaCloudApiAdapter` (stub).
- **Strategy / Template Method** — Resolución de plantillas WhatsApp,
  exportación a XLSX.
- **Builder** — `Customer.Builder`, `Vehicle.Builder`, etc., para
  construcción fluida de agregados.
- **Value Objects inmutables** — `Address`, `Money`, `DateRange`, etc.

### Event Storming (resumen)

Eventos de dominio identificados:

```
TenantRegistered → PlanSelected → CheckoutStarted → CheckoutCompleted
                                                    ↓
                                         WorkOrderCreated → WorkOrderAssigned
                                                              ↓
                                                       WorkOrderCompleted
                                                              ↓
                                                      ReminderScheduled
                                                              ↓
                                              NotificationDispatched
```

## Bounded contexts

| BC                | Responsabilidad                                                |
| ----------------- | -------------------------------------------------------------- |
| `analytics`       | KPIs, reportes, exportación Excel                              |
| `auth`            | Usuarios, JWT, registro, login, refresh                        |
| `billing`         | Planes, checkout mock-Stripe, webhook, suscripción             |
| `catalog`         | Categorías de partes y compatibilidades                        |
| `publicapi`       | Endpoints públicos sin autenticación (landing, planes)         |
| `reminder`        | Recordatorios automáticos                                      |
| `sales`           | Clientes                                                       |
| `shared`          | ApiResponse, GlobalExceptionHandler, OpenApiConfig             |
| `tenant`          | Talleres (multi-tenancy)                                       |
| `vehicle`         | Vehículos                                                      |
| `whatsapp`        | Mensajería WhatsApp (mock + stub live)                         |
| `workorder`       | Órdenes de trabajo                                             |

## Inicio rápido

### Pre-requisitos

- **JDK 17+** (Temurin recomendado)
- **Maven 3.9+** (o usar el wrapper `./mvnw`)
- **Node 20+** + **pnpm 9+** (solo para desarrollo del frontend)
- **MySQL 8** (opcional — por defecto usa H2 en memoria)

### Backend (perfil dev, H2 en memoria)

```bash
# Compilar y empaquetar (incluye el bundle Angular compilado)
./mvnw clean package

# Correr el JAR
java -jar target/sistema-innovador-0.0.1-SNAPSHOT.jar

# O alternativamente
./mvnw spring-boot:run
```

Levanta en `http://localhost:8586`. Swagger UI en
`http://localhost:8586/swagger-ui/index.html`.

### Backend con MySQL real

```bash
SPRING_PROFILES_ACTIVE=mysql \
SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/autotaller?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC' \
SPRING_DATASOURCE_USERNAME=root \
SPRING_DATASOURCE_PASSWORD=root \
java -jar target/sistema-innovador-0.0.1-SNAPSHOT.jar
```

### Frontend (desarrollo)

```bash
cd frontend
pnpm install
pnpm start    # ng serve → http://localhost:4200
```

`proxy.conf.json` redirige `/api/**` al backend en `:8586`.

### Todo con Docker Compose

```bash
docker compose up --build
# → http://localhost:8586 (backend + Swagger UI)
# → http://localhost:8080 (frontend nginx)
# → localhost:3306 (MySQL)
```

## Documentación de la API

| Recurso                          | URL                                       |
| -------------------------------- | ----------------------------------------- |
| Swagger UI (interactivo)         | `http://localhost:8586/swagger-ui/index.html` |
| OpenAPI spec (JSON)              | `http://localhost:8586/v3/api-docs`       |
| OpenAPI spec (YAML)              | `http://localhost:8586/v3/api-docs.yaml`  |

Los endpoints protegidos usan JWT bearer. Para probar:

1. `POST /api/auth/register` → crea usuario
2. `POST /api/auth/login` → recibe `accessToken`
3. Click en "Authorize" en Swagger UI → pega el token

## Tests

```bash
./mvnw test
./mvnw verify   # ejecuta también los integration tests con H2
```

Cobertura con JaCoCo (artefacto disponible en GitHub Actions):

```bash
./mvnw verify jacoco:report
# Reporte: target/site/jacoco/index.html
```

## Despliegue

Ver [DEPLOY.md](./DEPLOY.md) para instrucciones detalladas paso a paso:

- **Render** (recomendado, free tier)
- **Railway** (sencillo)
- **Fly.io** (más control)
- **VPS con Docker Compose** (control total)

## CI/CD

Workflows en `.github/workflows/`:

- **`ci.yml`** — En cada push/PR a `main`:
  1. Compila frontend (`pnpm build`)
  2. Ejecuta tests backend (JUnit5 + H2)
  3. Sube reporte JaCoCo como artefacto
  4. En tags `v*`, construye y publica imágenes a
     `ghcr.io/zeryle/autotaller-backend` y
     `ghcr.io/zeryle/autotaller-frontend`

## Estructura del repo

```
.
├── backend/                      # (futuro split — hoy vive en la raíz)
├── frontend/                     # Angular 19 SPA
│   ├── src/app/                  # Componentes, servicios, guards
│   ├── nginx-spa.conf            # Config nginx con SPA fallback + proxy /api
│   └── Dockerfile
├── src/main/java/com/utp/myapp/
│   ├── analytics/                # BC: KPIs + Excel
│   ├── auth/                     # BC: JWT, users
│   ├── billing/                  # BC: Stripe-mock, plans, checkout
│   ├── catalog/                  # BC: Partes
│   ├── publicapi/                # BC: Landing pública
│   ├── reminder/                 # BC: Recordatorios
│   ├── sales/                    # BC: Clientes
│   ├── shared/                   # Cross-cutting: ApiResponse, ExceptionHandler, OpenAPI
│   ├── tenant/                   # BC: Talleres
│   ├── vehicle/                  # BC: Vehículos
│   ├── whatsapp/                 # BC: WhatsApp Cloud API
│   └── workorder/                # BC: Órdenes de trabajo
├── src/main/resources/
│   ├── application.properties    # Config común
│   ├── application-dev.properties   # H2
│   ├── application-mysql.properties # MySQL
│   └── static/                   # Bundle Angular (generado por build)
├── src/test/                     # Tests
├── .github/workflows/ci.yml      # Pipeline CI/CD
├── Dockerfile                    # Backend multi-stage
├── docker-compose.yml            # Stack local completo
├── pom.xml
└── README.md
```

## Licencia

Proyecto académico — Universidad Tecnológica del Perú (UTP), curso
Desarrollo Web Integrado. Uso educativo.