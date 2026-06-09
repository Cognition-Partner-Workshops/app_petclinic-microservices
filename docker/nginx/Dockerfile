FROM nginx:1.27-alpine

# Install curl so Docker health checks can probe the proxy.
RUN apk add --no-cache curl

COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
