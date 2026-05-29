export enum Role {
  CUSTOMER = 'CUSTOMER',
  VENDOR = 'VENDOR',
  ADMIN = 'ADMIN',
}

export interface User {
  id: string;
  name: string;
  email: string;
  role: Role;
  profileImageUrl?: string;
}

export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  vendorId: string;
  imageUrls: string[];
  variants: ProductVariant[];
  averageRating: number;
  reviewCount: number;
}

export interface ProductVariant {
  id: string;
  name: string; // e.g., 'Color', 'Size'
  options: VariantOption[];
}

export interface VariantOption {
  id: string;
  value: string; // e.g., 'Red', 'Large'
  priceModifier: number; // e.g., 5.00 for a premium color
}

export interface Order {
  id: string;
  userId: string;
  items: OrderItem[];
  total: number;
  status: string;
  createdAt: string;
}

export interface OrderItem {
  id: string;
  product: Product;
  quantity: number;
  price: number;
}

export interface CartItem {
  id: string;
  product: Product;
  quantity: number;
}
