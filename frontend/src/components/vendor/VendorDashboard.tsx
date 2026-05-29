import React from 'react';
import ProductList from './ProductList';
import CreateProductForm from './CreateProductForm';

const VendorDashboard = () => {
  return (
    <div className="container mx-auto px-4">
      <h1 className="text-3xl font-bold my-6">Vendor Dashboard</h1>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div>
          <h2 className="text-2xl font-bold mb-4">My Products</h2>
          <ProductList />
        </div>
        <div>
          <h2 className="text-2xl font-bold mb-4">Create New Product</h2>
          <CreateProductForm />
        </div>
      </div>
    </div>
  );
};

export default VendorDashboard;
