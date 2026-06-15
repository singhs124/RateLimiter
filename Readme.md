# 🚦 RateLimiter — Distributed Rate Limiting as a Service

RateLimiter is a **Rate Limiting as a Service** application. Instead of implementing rate limiting inside every one of your applications, you register once, get an API key, and call this service to check whether a given user of your application should be allowed or denied.

**Example:**
- You are building an API product (User A)
- Your customers (User B, User C...) call your APIs
- You want to limit User B to 10 requests/minute and User C to 100 requests/minute
- Instead of building this yourself, you call **RateLimiter** — it handles it for you

---

## ✨ Features

- **Multiple Rate Limiting Algorithms** — Token Bucket, Leaky Bucket, Fixed Window (distributed & local variants)
- **Multi-level Cache** — Caffeine (L1, in-memory) + Redis (L2, distributed) for ultra-low latency lookups
- **Distributed Support** — Lua scripts executed atomically in Redis, safe for multi-instance deployments
- **Plan-based Limits** — FREE, PRO, and ENTERPRISE plans with different capacities and refill rates
- **OTP-based Passwordless Auth** — Secure, stateless authentication using email OTP + JWT
- **API Key Management** — Generate, validate, and revoke API keys tied to your account
- **Per-client Rate Limiting** — Limit each of your end-users independently under your account

---

## 🏗️ Architecture Overview

```
┌──────────────────────────────────────────────┐
│               Your Application               │
│  (sends X-API-KEY + clientId to RateLimiter) │
└───────────────────┬──────────────────────────┘
                    │
                    ▼
┌──────────────────────────────────────────────┐
│            RateLimiter Service               │
│                                              │
│  ┌─────────────┐     ┌────────────────────┐  │
│  │ JWT Filter  │     │ API Key Filter      │  │
│  │ /apikey/**  │     │ /ratelimit/**       │  │
│  └─────────────┘     └────────────────────┘  │
│                                              │
│  ┌──────────────────────────────────────┐    │
│  │         Rate Limit Engine            │    │
│  │  RateLimiterRegistry (Strategy Map)  │    │
│  │  ┌────────────┐  ┌────────────────┐  │    │
│  │  │ Token      │  │ Leaky Bucket   │  │    │
│  │  │ Bucket     │  │ (Distributed)  │  │    │
│  │  └────────────┘  └────────────────┘  │    │
│  └──────────────────────────────────────┘    │
│                                              │
│  ┌──────────────────────────────────────┐    │
│  │         Cache Layer                  │    │
│  │   Caffeine (L1) → Redis (L2) → DB   │    │
│  └──────────────────────────────────────┘    │
└──────────────────────────────────────────────┘
```

---

## 🚀 Getting Started

### Prerequisites

- [Docker](https://www.docker.com/) and [Docker Compose](https://docs.docker.com/compose/) installed on your system
- No other setup required — Redis and the application run inside containers

### Installation

```bash
# 1. Clone the repository
git clone https://github.com/your-username/ratelimiter.git
cd ratelimiter

## 2. Configuration
2.1 Create a `.env` file in the root directory of the project
2.2 Generate 2 strong secret keys (use `openssl rand -hex 32` or any password generator)
2.3 Add the following to your `.env` file:
      JWT_SECRET_KEY=your_strong_jwt_secret_key_here
      API_SECRET_KEY=your_strong_api_secret_key_here
# 3. Build and start all services
docker compose up --build
```

The application will be available at `http://localhost:8080`.

**Services started by Docker Compose:**
| Service | Port |
|---|---|
| RateLimiter API | `8080` |
| Redis | `6379` |
| PostgreSQL | `5432` |

---

## 📖 How to Use — Step by Step

### Step 1: Register Your Account

```bash
# Initiate registration — OTP sent to your email [Currently you need to check OTP in console logs of rateLimit container, email service is still in Progress]
curl -X POST http://localhost:8080/auth/register/initiate/ \
  -H "Content-Type: application/json" \
  -d '{ "email": "you@example.com" }'
```

```bash
# Verify OTP to complete registration
curl -X POST http://localhost:8080/auth/register/verify/ \
  -H "Content-Type: application/json" \
  -d '{ "email": "you@example.com", "otp": "123456" }'

# Response:
# { "access_token": "<jwt_token>", "refresh_token": "<jwt_token>", "created_at": "<time>" }
```

---

### Step 2: Generate Your API Key

Use the JWT token received from registration/login:

```bash
curl -X POST http://localhost:8080/api/v1/app/apikey/generateApiKey \
  -H "Authorization: Bearer <jwt_token>"

# Response:
# { "apiKey": "rl_xxxxxxxxxxxxxxxxxxxxxxxx" }
# ⚠️  Save this key — it is shown only once
```

---

### Step 3: Check Rate Limits From Your Application

Use your API key in the `X-API-KEY` header. Pass your end-user's identifier in the request body:

```bash
curl -X POST http://localhost:8080/api/v1/ratelimit/checkLimit \
  -H "X-API-KEY: rl26_xxxxxxxxxxxxxxxxxxxxxxxx" \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": "end-user-123",
    "algo": "DIST_TOKEN_BUCKET"
  }'

# Allowed Response (200):
# { "status": "ALLOWED" }

# Denied Response (429):
# { "status": "RATE LIMITED" }
```

**`clientId`** is the identifier of your end-user — each client gets their own independent rate limit bucket under your account.


## 📋 API Reference

### Auth Endpoints (Public)

| Method | Endpoint | Description | Body |
|---|---|---|---|
| `POST` | `/auth/register/initiate` | Start registration, send OTP | `{ "email": "..." }` |
| `POST` | `/auth/register/verify` | Verify OTP, create account, get JWT | `{ "email": "...", "otp": "..." }` |
| `POST` | `/auth/login/initiate` | Start login, send OTP | `{ "email": "..." }` |
| `POST` | `/auth/login/verify` | Verify OTP, get JWT | `{ "email": "...", "otp": "..." }` |

---

### API Key Endpoints (Requires JWT)

Pass JWT as `Authorization: Bearer <token>` header.

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/apikey/generate` | Generate a new API key for your account |
| `GET` | `/apikey/status` | Check status of your current API key |
| `DELETE` | `/apikey/revoke` | Revoke your current API key |

---

### Rate Limit Endpoints (Requires API Key)

Pass your API key as `X-API-KEY` header.

| Method | Endpoint | Description | Body |
|---|---|---|---|
| `POST` | `/api/v1/ratelimit/checkLimit` | Check and consume a rate limit token | `{ "clientId": "...", "algo": "..."}` |

**Request Body Fields:**

| Field | Type | Required | Description |
|---|---|---|---|
| `clientId` | `string` | ✅ | Your end-user's unique identifier |
| `algo` | `string` | ✅ | Rate limiting algorithm to use (see below) |

---

## ⚙️ Rate Limiting Algorithms

| Algorithm | Key | Description |
|---|---|---|
| Distributed Token Bucket | `DIST_TOKEN_BUCKET` | Tokens refill at a steady rate. Allows bursts up to capacity. Best for general API rate limiting. |
| Distributed Leaky Bucket | `DIST_LEAKY_BUCKET` | Requests processed at a constant rate. Smooths out bursts. Best for consistent throughput. |
| Token Bucket | `TOKEN_BUCKET` | In-memory token bucket. Use for single-instance deployments. |
| Fixed Window | `FIXED_WINDOW` | Counts requests in fixed time windows. Simple and predictable. |

---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Spring Boot 3 | Application framework |
| Spring Security | Authentication & authorization |
| Redis | Distributed cache + Lua script execution for atomic rate limiting |
| Caffeine | In-process L1 cache |
| PostgreSQL | Persistent storage |
| JWT (JJWT) | Stateless session tokens |
| Docker + Docker Compose | Containerized deployment |

---

## 📝 Environment Variables

| Variable | Description | REQUIRED? |
|---|---|-----------|
| `JWT_SECRET_KEY` | Jwt Secret| REQUIRED  |
| `AES_SECRET_KEY` | AES Encryption Secret | REQUIRED  |

---