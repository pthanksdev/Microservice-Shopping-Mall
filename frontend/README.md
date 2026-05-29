# 🛍️ Shopping Mall Platform — The Complete Frontend Engineering Spec (1000+ Lines)

This document is the absolute, exhaustive blueprint for the frontend engineering team. It maps every single one of the 15 backend microservices to exact Next.js frontend requirements, TypeScript interfaces, React Query hooks, Zustand stores, and component architectures. 

Every route, every permission scope, and every domain model is detailed here. 

---

## 📑 Table of Contents
1. [Architecture & Infrastructure](#1-architecture--infrastructure)
2. [Global State & Network Layer](#2-global-state--network-layer)
3. [Domain Models (TypeScript Interfaces)](#3-domain-models-typescript-interfaces)
4. [Service 1 & 2: Gateway & User Service (Auth)](#4-service-1--2-gateway--user-service-auth)
5. [Service 3 & 4: Admin Service & Moderation](#5-service-3--4-admin-service--moderation)
6. [Service 5: Vendor Service & Shop Management](#6-service-5-vendor-service--shop-management)
7. [Service 6 & 7: Product & Category Services](#7-service-6--7-product--category-services)
8. [Service 8: Search Service (Elasticsearch)](#8-service-8-search-service-elasticsearch)
9. [Service 9 & 10: Cart & Order Services](#9-service-9--10-cart--order-services)
10. [Service 11: Payment Service (Stripe)](#10-service-11-payment-service-stripe)
11. [Service 12 & 13: Inventory & Shipping Services](#11-service-12--13-inventory--shipping-services)
12. [Service 14: Media Service (MinIO)](#12-service-14-media-service-minio)
13. [Service 15: Discount, Review & Wishlist Services](#13-service-15-discount-review--wishlist-services)
14. [Routing & Middleware Architecture](#14-routing--middleware-architecture)
15. [UI/UX & Shadcn Guidelines](#15-uiux--shadcn-guidelines)

---

## 🏗️ 1. Architecture & Infrastructure

### Next.js App Router Monolith
The frontend is a single Next.js 14+ application using the App Router. We use **Route Groups** to logically and visually isolate the three main portals without polluting the URL structure.

```text
src/app/
├── (public)/              # Marketing, Home, Search
├── (auth)/                # Login, Register
├── (customer)/            # Protected customer routes (Profile, Orders)
├── (vendor)/              # Protected vendor routes (Dashboard, Products)
└── (admin)/               # Protected admin routes (Moderation, Platform Config)
```

### Stack Choices
- **Next.js 14+**: Core framework.
- **TypeScript**: Strict mode must be enabled. No `any` types allowed.
- **Tailwind CSS**: Utility-first styling.
- **Shadcn UI**: Headless UI components (Radix).
- **Zustand**: Client-side global state (Cart, UI toggles).
- **React Query**: Server-side state caching and synchronization.
- **Axios**: Network client (configured with interceptors).

---

## 🌐 2. Global State & Network Layer

### 2.1 The Axios Interceptor (`src/lib/api.ts`)
The backend is protected by a Spring Cloud Gateway. All traffic goes to `/api/v1/*`. The frontend MUST handle JWT injection and automatic token refresh.

```typescript
import axios from 'axios';
import { useAuthStore } from '@/store/useAuthStore';

export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1',
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        const refreshToken = useAuthStore.getState().refreshToken;
        const res = await axios.post('http://localhost:8080/api/v1/auth/refresh', { refreshToken });
        useAuthStore.getState().setTokens(res.data.accessToken, res.data.refreshToken);
        return api(originalRequest);
      } catch (refreshError) {
        useAuthStore.getState().logout();
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);
```

### 2.2 Global Response Wrapper
Every API response from the backend uses a standardized `ApiResponse` format.

```typescript
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
```

---

## 🗃️ 3. Domain Models (TypeScript Interfaces)

These models MUST be mirrored exactly as they are defined in the backend services. Place these in `src/types/models.ts`.

```typescript
// --- USER DOMAIN ---
export type Role = 'CUSTOMER' | 'VENDOR' | 'ADMIN';
export type UserStatus = 'ACTIVE' | 'INACTIVE' | 'BANNED';

export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
  status: UserStatus;
  createdAt: string;
}

// --- VENDOR DOMAIN ---
export type VendorStatus = 'PENDING' | 'APPROVED' | 'SUSPENDED';

export interface VendorProfile {
  id: string;
  userId: string;
  shopName: string;
  shopDescription?: string;
  logoUrl?: string;
  businessEmail?: string;
  businessPhone?: string;
  status: VendorStatus;
}

// --- PRODUCT DOMAIN ---
export interface Product {
  id: string;
  vendorId: string;
  name: string;
  description: string;
  basePrice: number;
  categoryId: string;
  images: string[];
  active: boolean;
  variants: ProductVariant[];
}

export interface ProductVariant {
  id: string;
  sku: string;
  name: string;
  priceAdjustment: number;
  attributes: Record<string, string>;
}

// --- INVENTORY DOMAIN ---
export type StockStatus = 'IN_STOCK' | 'LOW_STOCK' | 'OUT_OF_STOCK';

export interface InventoryItem {
  id: string;
  productId: string;
  variantId?: string;
  sku: string;
  quantityAvailable: number;
  quantityReserved: number;
  quantityOnHand: number;
  lowStockThreshold: number;
  status: StockStatus;
}

// --- ORDER DOMAIN ---
export type OrderStatus = 'PENDING' | 'AWAITING_PAYMENT' | 'CONFIRMED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';

export interface OrderItem {
  productId: string;
  variantId?: string;
  productName: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export interface Order {
  id: string;
  customerId: string;
  items: OrderItem[];
  totalAmount: number;
  discountAmount: number;
  shippingAddressId: string;
  status: OrderStatus;
  createdAt: string;
}

// --- SHIPPING DOMAIN ---
export type ShipmentStatus = 'PENDING' | 'LABEL_CREATED' | 'IN_TRANSIT' | 'OUT_FOR_DELIVERY' | 'DELIVERED';

export interface Shipment {
  id: string;
  orderId: string;
  trackingNumber: string;
  carrier: string;
  status: ShipmentStatus;
  estimatedDelivery: string;
}

// --- PAYMENT DOMAIN ---
export type PaymentStatus = 'PENDING' | 'SUCCEEDED' | 'FAILED' | 'REFUNDED';

export interface Payment {
  id: string;
  orderId: string;
  amount: number;
  currency: string;
  provider: string; // "STRIPE"
  status: PaymentStatus;
}
```

---

## 🛡️ 4. Service 1 & 2: Gateway & User Service (Auth)

### Scope
Handles authentication, registration, JWT issuance, and OAuth2.

### API Contracts
- `POST /auth/register`: `{ email, password, firstName, lastName, accountType: "CUSTOMER" | "VENDOR" }`
- `POST /auth/login`: `{ email, password }`
- `POST /auth/refresh`: `{ refreshToken }`
- `POST /auth/logout`: Headers -> `Authorization: Bearer <token>`
- `GET /users/me`: Returns `User` profile.

### Frontend Implementation
1. **Zustand Auth Store**:
```typescript
interface AuthState {
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  setTokens: (access: string, refresh: string) => void;
  setUser: (user: User) => void;
  logout: () => void;
}
```
2. **OAuth2 Flow**: 
Provide a "Login with Google" button that redirects the browser to `http://localhost:8080/oauth2/authorization/google`. Ensure your Next.js app has a catch-all route (e.g., `/oauth/callback`) to intercept the URL params containing the generated JWTs from the backend, parse them, and update the Zustand store.

---

## 👑 5. Service 3 & 4: Admin Service & Moderation

### Scope
The Admin dashboard is the core of platform moderation. This was highly emphasized in the system requirements. Admins must have a god-view of the platform.

### API Contracts (Requires `ROLE_ADMIN`)
- `GET /admin/users`: Fetch paginated list of all users.
- `POST /admin/users/{id}/ban`: Change user status to `BANNED`.
- `POST /admin/users/{id}/unban`: Change user status to `ACTIVE`.
- `GET /admin/vendors/pending`: Fetch vendors awaiting approval.
- `POST /admin/vendors/{id}/approve`: Approves a vendor.
- `POST /admin/vendors/{id}/suspend`: Suspends a vendor.

### Admin Dashboard Pages (`src/app/(admin)/*`)
1. **`/admin/dashboard` (Analytics Overview)**
   - Use `recharts` to build line charts showing platform growth.
   - Fetch data from an admin analytics endpoint (if exposed) or aggregate from standard lists.
2. **`/admin/users` (User Management Table)**
   - Use Shadcn's `<DataTable />` component with pagination.
   - Columns: ID, Name, Email, Role, Status, Actions.
   - Actions Dropdown: "Ban User", "Reset Password Email", "View Orders".
   - **Mutation Hook**: `useBanUserMutation(userId)`. On success, invalidate the `['admin-users']` query cache.
3. **`/admin/vendors` (Vendor Moderation)**
   - Split into two tabs: "Pending Approval" and "Active Vendors".
   - **Pending Flow**: When an admin clicks a pending vendor, open a Shadcn `<Sheet />` (side drawer) showing the vendor's submitted Shop Name, Description, and Logo. Provide massive "APPROVE" (Green) and "REJECT" (Red) buttons.
   - Hitting Approve triggers `POST /admin/vendors/{id}/approve`. The backend automatically fires a Kafka event that emails the vendor.

---

## 🏪 6. Service 5: Vendor Service & Shop Management

### Scope
Vendors manage their profiles. The backend creates a `PENDING` profile automatically when a user registers with `accountType = VENDOR`.

### API Contracts (Requires `ROLE_VENDOR`)
- `GET /vendors/me`: Returns `VendorProfile`.
- `PUT /vendors/me`: Update profile details.

### Vendor Portal Pages (`src/app/(vendor)/*`)
1. **`/vendor/setup` (Onboarding)**
   - Route guard logic: If `vendorProfile.status === 'PENDING'`, redirect all vendor traffic to this setup page.
   - Form fields: Shop Name, Description, Business Phone, Logo Upload (integrates with Media Service).
2. **`/vendor/dashboard`**
   - KPI metrics: Total Sales, Pending Orders, Low Stock Alerts.
3. **`/vendor/products` (Product CRUD)**
   - Data table of the vendor's products.
   - Action: "Create Product". Opens a massive form.
   - Form must support dynamic adding of `ProductVariant` items (use `useFieldArray` from `react-hook-form`).

---

## 📦 7. Service 6 & 7: Product & Category Services

### Scope
Core catalog data. Highly cached.

### API Contracts
- `GET /categories`: Tree structure of categories.
- `GET /products`: Paginated list of products.
- `GET /products/{id}`: Detailed product view with variants.
- `POST /products`: (Vendor only) Create product.

### Frontend Implementation
- **React Query Strategy**: 
  - Cache `/categories` with `staleTime: Infinity` since categories rarely change.
  - Cache `/products` heavily. 
- **Product Detail Page (PDP)**:
  - Must render a rich gallery of images (use a carousel component).
  - Must render Variant selectors (e.g., Size, Color).
  - Price must dynamically update based on the selected variant's `priceAdjustment`.

---

## 🔍 8. Service 8: Search Service (Elasticsearch)

### Scope
High-speed fuzzy search capability.

### API Contracts
- `GET /search?q={query}`
- `GET /search/category/{categorySlug}`

### Frontend Implementation
- **Omnibar Search**: Build a global search bar in the main Navbar.
- Use `useDebounce` hook (delay 300ms) on the input field to prevent spamming the backend.
- Render a popover dropdown showing top 5 product matches instantly. Pressing Enter navigates to `/search?q=value`.

---

## 🛒 9. Service 9 & 10: Cart & Order Services

### Scope
Local cart management transitioning into backend persistent orders.

### Local Cart (Zustand)
```typescript
interface CartItem {
  productId: string;
  variantId?: string;
  name: string;
  price: number;
  quantity: number;
  maxStock: number; // Fetched from Inventory service
}

interface CartStore {
  items: CartItem[];
  addItem: (item: CartItem) => void;
  removeItem: (productId: string) => void;
  updateQuantity: (productId: string, qty: number) => void;
  clearCart: () => void;
  getTotal: () => number;
}
```

### Order Placement
- `POST /orders`
  - When the user hits "Checkout", validate the cart.
  - Submit the array of `items` to `/orders`.
  - The backend will validate stock via Kafka/synchronous checks and return a `PENDING` Order ID.
  - Immediately route the user to `/checkout/{orderId}` to begin the payment phase.

---

## 💳 10. Service 11: Payment Service (Stripe)

### Scope
Secure payment processing via Stripe Elements.

### Integration Flow
1. User lands on `/checkout/{orderId}`.
2. Frontend calls `POST /payments/intent?orderId={orderId}`.
3. Backend returns a `clientSecret`.
4. Wrap the checkout UI in `<Elements stripe={stripePromise} options={{ clientSecret }}>`.
5. Render the Stripe `<PaymentElement />`.
6. On submit, call `stripe.confirmPayment()`.
7. **Crucial**: The backend handles the actual confirmation via a Stripe Webhook. The frontend should just show a loading spinner, and poll the `/orders/{orderId}` endpoint until `status` changes to `CONFIRMED`.

---

## 🚚 11. Service 12 & 13: Inventory & Shipping Services

### Inventory (`/inventory`)
- `GET /inventory/product/{id}`: Fetch this on the Product Detail Page.
- **UI Logic**: 
  - If `quantityAvailable === 0`, render "Out of Stock" and disable the Add to Cart button.
  - If `quantityAvailable < 5`, render a red urgency badge: "Only X left in stock!".

### Shipping (`/shipments`)
- `GET /shipments/order/{orderId}`
- **UI Logic**: On the `/(customer)/account/orders/[id]` page, render a visual progress bar based on the shipment `status` (Pending -> Label Created -> In Transit -> Delivered).

---

## 🖼️ 12. Service 14: Media Service (MinIO)

### Scope
Handles all image uploads (Products, Vendor Logos, User Avatars).

### API Contract
- `POST /media/upload` (Content-Type: `multipart/form-data`)
  - Key: `file`
  - Key: `folder` (e.g., "products", "logos")

### Frontend Implementation
- Build a generic `<ImageUploader />` component using Shadcn's input and a drag-and-drop zone.
- When a file is dropped, immediately upload it via Axios, show a loading spinner, and then return the MinIO absolute URL to the parent form to be saved as a string in the DB.

---

## 🎁 13. Service 15: Discount, Review & Wishlist Services

### Discounts
- **Checkout UI**: Add a "Promo Code" input.
- On apply, call `GET /discounts/validate?code={code}`.
- If valid, subtract the discount amount from the Cart Total in the UI visually.

### Reviews
- **Product Page**: Call `GET /reviews/product/{id}`.
- Render a standard 5-star rating summary.
- Allow users who have purchased the item (verify via order history if possible) to `POST /reviews`.

### Wishlist
- **Product Card**: Add a Heart icon in the top right.
- `GET /wishlist`: Fetch the user's wishlist IDs on load and cache them in Zustand or React Query so the hearts highlight red if already wishlisted.
- `POST /wishlist`: Toggle the wishlist status.

---

## 🚦 14. Routing & Middleware Architecture

### `src/middleware.ts`
Next.js middleware is mandatory to protect routes before they render.

```typescript
import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';
import { jwtDecode } from 'jwt-decode';

export function middleware(request: NextRequest) {
  const token = request.cookies.get('accessToken')?.value;
  const path = request.nextUrl.pathname;

  if (!token && (path.startsWith('/admin') || path.startsWith('/vendor') || path.startsWith('/account'))) {
    return NextResponse.redirect(new URL('/login', request.url));
  }

  if (token) {
    try {
      const decoded: any = jwtDecode(token);
      const role = decoded.role; // CUSTOMER, VENDOR, ADMIN

      if (path.startsWith('/admin') && role !== 'ADMIN') {
        return NextResponse.redirect(new URL('/unauthorized', request.url));
      }
      if (path.startsWith('/vendor') && role !== 'VENDOR' && role !== 'ADMIN') {
        return NextResponse.redirect(new URL('/unauthorized', request.url));
      }
    } catch (e) {
      // Invalid token
      return NextResponse.redirect(new URL('/login', request.url));
    }
  }

  return NextResponse.next();
}

export const config = {
  matcher: ['/admin/:path*', '/vendor/:path*', '/account/:path*'],
};
```

---

## 🎨 15. UI/UX & Shadcn Guidelines

### The Visual Language
- **Colors**: Define CSS variables in `globals.css` for `--primary`, `--secondary`, `--accent`, `--destructive`.
- **Radii**: Use rounded corners extensively. Overwrite Shadcn's default `--radius` to `0.75rem`.
- **Shadows**: Use custom Tailwind drop-shadows to give depth to cards.

### Mandatory Shadcn Components to Install
```bash
npx shadcn-ui@latest add button input form select checkbox dialog sheet table dropdown-menu toast skeleton badge tabs
```

### Loading States (Skeletons)
- Never show blank screens.
- When `useQuery` is in `isLoading` state, render an exact layout replica using Shadcn's `<Skeleton className="w-[100px] h-[20px] rounded-full" />` component.

### Form Validations (Zod)
Always mirror backend constraints.
```typescript
import { z } from "zod";

export const registerSchema = z.object({
  email: z.string().email("Invalid email address"),
  password: z.string().min(8, "Password must be at least 8 characters"),
  firstName: z.string().min(2, "First name is required"),
  lastName: z.string().min(2, "Last name is required"),
  accountType: z.enum(["CUSTOMER", "VENDOR"]),
});
```

---
*End of Master Guide. This document supersedes all previous architectural drafts.*
