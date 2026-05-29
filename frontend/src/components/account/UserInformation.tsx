import React from 'react';
import { useAuthStore } from '@/store/useAuthStore';
import { api } from '@/lib/api';
import { useQuery } from '@tanstack/react-query';

const fetchUser = async () => {
  const { data } = await api.get('/users/me');
  return data;
};

const UserInformation = () => {
  const { user, setUser } = useAuthStore();
  const { data, isLoading, error } = useQuery({ 
    queryKey: ['user'], 
    queryFn: fetchUser, 
    onSuccess: (data) => setUser(data) 
  });

  if (isLoading) return <div>Loading...</div>;
  if (error) return <div>Error loading user data</div>;

  return (
    <div className="p-4 border rounded-md">
      <h3 className="text-lg font-bold">User Information</h3>
      <p><strong>Name:</strong> {user?.name}</p>
      <p><strong>Email:</strong> {user?.email}</p>
    </div>
  );
};

export default UserInformation;
