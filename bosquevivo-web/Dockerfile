FROM node:22-alpine AS build

WORKDIR /app
COPY bosquevivo-web/package*.json ./
RUN npm ci
COPY bosquevivo-web ./
ARG VITE_API_URL=http://localhost:8082
ARG VITE_IAM_URL=http://localhost:8089
ENV VITE_API_URL=$VITE_API_URL
ENV VITE_IAM_URL=$VITE_IAM_URL
RUN npm run build

FROM nginx:1.27-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY bosquevivo-web/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
