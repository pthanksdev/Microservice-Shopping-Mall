# 🛍️ Shopping Mall — Microservices Platform

A production-grade, multi-tenant e-commerce platform built with **Spring Boot 3.3 microservices**, **Apache Kafka**, **Elasticsearch**, **Redis**, and **MinIO** — fully containerized with Docker Compose and deployable to Kubernetes.

---

## 🏗️ Architecture

```
                    ┌──────────────────────────────┐
                    │         API Gateway            │
                    │   Spring Cloud Gateway :8080   │
                    │  JWT Auth · Rate Limit · CORS  │
                    └───────────────┬──────────────┘
                                    │
         ┌──────────────────────────┼──────────────────────────┐
         │                          │                           │
   ┌─────▼─────┐            ┌───────▼──────┐          ┌────────▼────────┐
   │user-service│            │product-service│          │  order-service  │
   │   :8081   │            │    :8082      │          │     :8083       │
   └───────────┘            └──────────────┘          └─────────────────┘
         ...and 11 more services
                                    │
                    ┌───────────────▼──────────────┐
                    │          Apache Kafka          │
                    │        (Event Bus)             │
                    └──────────────────────────────┘
```

## 📦 Services

| Service | Port | Description |
|---|---|---|
| **api-gateway** | 8080 | Routes, JWT auth, rate limiting |
| **user-service** | 8081 | Auth, profiles, OAuth2, roles |
| **product-service** | 8082 | Catalog, categories, variants |
| **order-service** | 8083 | Cart, checkout, order lifecycle |
| **payment-service** | 8084 | Stripe, transactions, payouts |
| **vendor-service** | 8085 | Shop registration, KYC, commission |
| **inventory-service** | 8086 | Stock, warehouses, reservations |
| **notification-service** | 8087 | Email, push, in-app notifications |
| **shipping-service** | 8088 | Couriers, tracking, labels |
| **review-service** | 8089 | Ratings, Q&A, moderation |
| **discount-service** | 8090 | Coupons, flash sales, bundles |
| **wishlist-service** | 8091 | Saved products, compare, alerts |
| **admin-service** | 8092 | Back-office: users, vendors, reports |
| **search-service** | 8093 | Elasticsearch full-text search |
| **media-service** | 8094 | Image upload, CDN, resize (MinIO) |

## 🔧 Infrastructure

| Component | Purpose | Port |
|---|---|---|
| PostgreSQL 16 | Primary database (14 DBs) | 5432 |
| Redis 7 | Cache + sessions + cart | 6379 |
| Apache Kafka | Async event streaming | 9092 |
| Elasticsearch 8 | Full-text product search | 9200 |
| MinIO | S3-compatible media storage | 9000/9001 |
| Zipkin | Distributed tracing | 9411 |

---

## 🚀 Quick Start (Docker Compose)

### Prerequisites
- Docker 24+ and Docker Compose v2
- 8GB RAM minimum recommended

### 1. Clone and configure
```bash
git clone <repo-url>
cd shopping-mall
cp .env.example .env
# Edit .env with your credentials (Stripe keys, OAuth2 client IDs, etc.)
```

### 2. Start full stack
```bash
cd infrastructure/docker
docker-compose up --build
```

### 3. Start in dev mode (with hot reload)
```bash
cd infrastructure/docker
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up
```

### 4. Verify all services are healthy
```bash
curl http://localhost:8080/actuator/health
```

---

## 📖 API Documentation (Swagger UI)

Once running, each service exposes its own Swagger UI:

| Service | Swagger URL |
|---|---|
| user-service | http://localhost:8081/swagger-ui.html |
| product-service | http://localhost:8082/swagger-ui.html |
| order-service | http://localhost:8083/swagger-ui.html |
| payment-service | http://localhost:8084/swagger-ui.html |
| vendor-service | http://localhost:8085/swagger-ui.html |
| inventory-service | http://localhost:8086/swagger-ui.html |
| notification-service | http://localhost:8087/swagger-ui.html |
| shipping-service | http://localhost:8088/swagger-ui.html |
| review-service | http://localhost:8089/swagger-ui.html |
| discount-service | http://localhost:8090/swagger-ui.html |
| wishlist-service | http://localhost:8091/swagger-ui.html |
| admin-service | http://localhost:8092/swagger-ui.html |
| search-service | http://localhost:8093/swagger-ui.html |
| media-service | http://localhost:8094/swagger-ui.html |

---

## ☸️ Kubernetes Deployment

### Prerequisites
- kubectl configured
- NGINX Ingress Controller installed
- Container images built and pushed to your registry

### Deploy
```bash
# Apply all manifests in order
kubectl apply -f infrastructure/k8s/namespaces/
kubectl apply -f infrastructure/k8s/configmaps/
kubectl apply -f infrastructure/k8s/secrets/
kubectl apply -f infrastructure/k8s/postgres/
kubectl apply -f infrastructure/k8s/redis/
kubectl apply -f infrastructure/k8s/kafka/
kubectl apply -f infrastructure/k8s/elasticsearch/
kubectl apply -f infrastructure/k8s/deployments/
kubectl apply -f infrastructure/k8s/services/
kubectl apply -f infrastructure/k8s/ingress/

# Verify
kubectl get pods -n mall
kubectl get services -n mall
```

---

## 📨 Kafka Topics

| Topic | Producer | Consumers |
|---|---|---|
| `user.registered` | user-service | notification-service, vendor-service |
| `product.created` | product-service | search-service, inventory-service |
| `product.updated` | product-service | search-service |
| `order.placed` | order-service | inventory-service, notification-service, shipping-service |
| `order.confirmed` | order-service | notification-service, shipping-service |
| `order.cancelled` | order-service | inventory-service, notification-service |
| `payment.succeeded` | payment-service | order-service, notification-service |
| `payment.failed` | payment-service | order-service, notification-service |
| `stock.reserved` | inventory-service | order-service |
| `stock.released` | inventory-service | wishlist-service |
| `shipment.created` | shipping-service | notification-service |
| `vendor.approved` | admin-service | vendor-service, notification-service |

---

## 🔐 Authentication

- **Email/Password**: `POST /api/v1/auth/register` (pick `CUSTOMER` or `VENDOR`) + `POST /api/v1/auth/login`
- **Google OAuth2**: `GET /oauth2/authorize/google`
- **GitHub OAuth2**: `GET /oauth2/authorize/github`
- All protected routes require `Authorization: Bearer <jwt_token>` header

---

## 🏗️ Project Structure

```
shopping-mall/
├── backend/                    # All 15 Spring Boot microservices
│   ├── api-gateway/
│   ├── user-service/
│   ├── product-service/
│   ├── ... (13 more)
│   └── common/                 # Shared library (DTOs, exceptions, utils)
├── infrastructure/
│   ├── docker/
│   │   ├── docker-compose.yml
│   │   └── docker-compose.dev.yml
│   └── k8s/                    # Plain Kubernetes YAML (no Helm)
├── .env.example
├── .gitignore
└── README.md
```

---

## 🧪 Running Tests

```bash
# Run tests for a specific service
cd backend/user-service
mvn test

# Run all tests
cd backend
mvn test --projects user-service,product-service,...
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit changes: `git commit -m 'Add my feature'`
4. Push to branch: `git push origin feature/my-feature`
5. Open a Pull Request
# Microservice-Shopping-Mall
