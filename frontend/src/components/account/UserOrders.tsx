'use client';

import React, { useState, useEffect } from 'react';
import api from '@/lib/api'; // Corrected import
import { useRouter } from 'next/navigation'; // Import useRouter

interface Order {
  id: string;
  orderNumber: string;
  totalAmount: number;
  status: string;
  createdAt: string;
}

export default function UserOrders() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const router = useRouter(); // Initialize useRouter

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        setLoading(true);
        setError(null);
        // Assuming an endpoint to fetch orders for the logged-in user
        // If the user is not logged in, this might result in an error or empty array
        const response = await api.get('/orders'); 
        setOrders(response.data);
      } catch (err) {
        // Handle potential auth errors or other server issues
        if (err.response && err.response.status === 401) {
          router.push('/login'); // Redirect to login if unauthorized
        } else {
          setError('Failed to load orders. Please try again later.');
          console.error('Error fetching orders:', err);
        }
      } finally {
        setLoading(false);
      }
    };

    fetchOrders();
  }, [router]); // Include router in dependency array

  if (loading) {
    return <div className="text-center p-4">Loading orders...</div>;
  }

  if (error) {
    return <div className="text-center p-4 text-red-500">{error}</div>;
  }

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4">My Orders</h1>
      {orders.length === 0 ? (
        <p>You have not placed any orders yet.</p>
      ) : (
        <div className="grid grid-cols-1 gap-4">
          {orders.map((order) => (
            <div key={order.id} className="border p-4 rounded-lg shadow">
              <div className="flex justify-between items-center mb-2">
                <h2 className="text-lg font-semibold">Order #{order.orderNumber}</h2>
                <span className={`px-2 py-1 rounded text-white ${
                  order.status === 'Shipped' ? 'bg-green-500' :
                  order.status === 'Processing' ? 'bg-yellow-500' :
                  order.status === 'Delivered' ? 'bg-blue-500' :
                  order.status === 'Cancelled' ? 'bg-red-500' : // Added Cancelled status
                  'bg-gray-500'
                }`}>
                  {order.status}
                </span>
              </div>
              <p className="text-sm text-gray-600 mb-1">Date: {new Date(order.createdAt).toLocaleDateString()}</p>
              <p className="text-lg font-bold">Total: ${order.totalAmount.toFixed(2)}</p>
              {/* Optional: Add a link to view order details */}
              <button 
                onClick={() => router.push(`/orders/${order.id}`)} 
                className="mt-2 text-blue-500 hover:underline">
                View Details
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
