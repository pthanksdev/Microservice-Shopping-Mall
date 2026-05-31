'use client';

import React, { useState, useEffect } from 'react';
import api from '@/lib/api'; // Corrected import
import { useAuth } from '@/hooks/useAuth'; // Assuming you have a useAuth hook
import { useRouter } from 'next/navigation'; // Import useRouter

interface UserInfo {
  id: string;
  name: string;
  email: string;
  role: string;
  address?: string; // Optional address field
  phone?: string; // Optional phone field
}

export default function UserInformation() {
  const [userInfo, setUserInfo] = useState<UserInfo | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { user, isAuthenticated } = useAuth(); // Get user from auth context and check authentication status
  const router = useRouter();

  useEffect(() => {
    // Redirect to login if not authenticated
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    const fetchUserInfo = async () => {
      if (!user) {
        setLoading(false);
        return;
      }
      try {
        setLoading(true);
        setError(null);
        // Assuming an endpoint to fetch user details by ID, or a /me endpoint
        const response = await api.get(`/users/${user.id}`); 
        setUserInfo(response.data);
      } catch (err) {
        setError('Failed to load user information.');
        console.error('Error fetching user info:', err);
        // Handle potential auth errors
        if (err.response && err.response.status === 401) {
            router.push('/login');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchUserInfo();
  }, [user, isAuthenticated, router]); // Add dependencies

  if (loading) {
    return <div className="text-center p-4">Loading user information...</div>;
  }

  if (error) {
    return <div className="text-center p-4 text-red-500">{error}</div>;
  }

  if (!userInfo) {
    return <div className="text-center p-4">No user information available.</div>;
  }

  return (
    <div className="max-w-md mx-auto p-4 border rounded-lg shadow-md">
      <h1 className="text-2xl font-bold mb-4 text-center">User Profile</h1>
      <div className="space-y-3">
        <div>
          <span className="font-semibold text-gray-700 block mb-1">Name:</span>
          <p className="text-gray-900">{userInfo.name}</p>
        </div>
        <div>
          <span className="font-semibold text-gray-700 block mb-1">Email:</span>
          <p className="text-gray-900">{userInfo.email}</p>
        </div>
        <div>
          <span className="font-semibold text-gray-700 block mb-1">Role:</span>
          <p className="text-gray-900 capitalize">{userInfo.role}</p>
        </div>
        {/* Display optional fields if they exist */}
        {userInfo.address && (
          <div>
            <span className="font-semibold text-gray-700 block mb-1">Address:</span>
            <p className="text-gray-900">{userInfo.address}</p>
          </div>
        )}
        {userInfo.phone && (
          <div>
            <span className="font-semibold text-gray-700 block mb-1">Phone:</span>
            <p className="text-gray-900">{userInfo.phone}</p>
          </div>
        )}
      </div>
      {/* Add an edit profile button if needed */}
      <div className="text-center mt-6">
        <button 
          onClick={() => router.push(`/profile/edit/${userInfo.id}`)} 
          className="bg-indigo-600 hover:bg-indigo-700 text-white font-medium py-2 px-4 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
        >
          Edit Profile
        </button>
      </div>
    </div>
  );
}
