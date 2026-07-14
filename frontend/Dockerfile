# ============================================================================
# Stage 1 — Build the Angular 19 SPA
# ============================================================================
FROM node:20-alpine AS build
WORKDIR /workspace

# pnpm via corepack
RUN corepack enable && corepack prepare pnpm@latest --activate

# Install dependencies first so this layer caches independently of source changes
COPY package.json pnpm-lock.yaml* ./
RUN pnpm install --frozen-lockfile

# Then bring in the rest of the source
COPY . ./

# angular.json `ng build` produces dist/browser
RUN pnpm build

# ============================================================================
# Stage 2 — Runtime: nginx serving the static SPA with SPA-fallback
# ============================================================================
FROM nginx:1.27-alpine AS runtime

# Replace the default nginx config with one that supports SPA fallback
# (all unknown routes fall back to /index.html so Angular Router can take over)
RUN rm -f /etc/nginx/conf.d/default.conf
COPY nginx-spa.conf /etc/nginx/conf.d/default.conf

# Copy built SPA from the previous stage
COPY --from=build /workspace/dist/browser /usr/share/nginx/html

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD wget -q -O /dev/null http://localhost/ || exit 1

LABEL org.opencontainers.image.title="AutoTaller Frontend" \
      org.opencontainers.image.description="Angular 19 SPA served by nginx with SPA fallback to /index.html" \
      org.opencontainers.image.source="https://github.com/Zeryle/sistema-innovador"