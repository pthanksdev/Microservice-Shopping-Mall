'use client';

import React, { useState, useEffect } from 'react';
import api from '@/lib/api'; // Corrected import

interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  imageUrl: string;
  stock: number;
}

export default function ProductCard({ product }: { product: Product }) {
  const router = useRouter();

  const handleViewProduct = () => {
    router.push(`/products/${product.id}`);
  };

  const handleAddToCart = async () => {
    try {
      // Assuming you have an add-to-cart endpoint
      await api.post('/cart/items', { productId: product.id, quantity: 1 });
      alert('Product added to cart!');
      // Optionally, you could trigger a cart update event or refetch cart data
    } catch (error) {
      console.error('Error adding product to cart:', error);
      alert('Failed to add product to cart.');
    }
  };

  return (
    <div className="border p-4 rounded-lg shadow-md hover:shadow-lg transition-shadow duration-300 ease-in-out">
      <img
        src={product.imageUrl}
        alt={product.name}
        className="w-full h-48 object-cover rounded-t-lg cursor-pointer"
        onClick={handleViewProduct}
      />
      <div className="p-4">
        <h3 className="text-lg font-semibold mb-2 cursor-pointer" onClick={handleViewProduct}>
          {product.name}
        </h3>
        <p className="text-gray-600 mb-3 line-clamp-2">{product.description}</p>
        <p className="text-lg font-bold text-indigo-600 mb-4">${product.price.toFixed(2)}</p>
        <div className="flex justify-between items-center">
          <button
            onClick={handleAddToCart}
            className="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50"
          >
            Add to Cart
          </button>
          {/* Optional: Display stock info */}
          <span className={`text-sm ${product.stock > 0 ? 'text-green-600' : 'text-red-600'}`}>
            {product.stock > 0 ? `${product.stock} in stock` : 'Out of stock'}
          </span>
        </div>
      </div>
    </div>
  );
}
