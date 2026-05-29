import React from 'react';
import VendorApprovalQueue from './VendorApprovalQueue';

const AdminDashboard = () => {
  return (
    <div className="container mx-auto px-4">
      <h1 className="text-3xl font-bold my-6">Admin Dashboard</h1>
      <VendorApprovalQueue />
    </div>
  );
};

export default AdminDashboard;
