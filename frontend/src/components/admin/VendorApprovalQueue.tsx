'use client';

import React, { useState, useEffect } from 'react';
import api from '@/lib/api'; // Corrected import
import { useRouter } from 'next/navigation'; // Import useRouter

interface VendorApproval {
  id: string;
  vendorName: string;
  email: string;
  status: 'pending' | 'approved' | 'rejected';
  appliedAt: string;
  companyInfo?: string; // Optional field for more details
}

export default function VendorApprovalQueue() {
  const [approvals, setApprovals] = useState<VendorApproval[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const router = useRouter();

  useEffect(() => {
    const fetchApprovals = async () => {
      try {
        setLoading(true);
        setError(null);
        const response = await api.get('/admin/vendor-approvals'); // Endpoint for vendor approvals
        setApprovals(response.data);
      } catch (err) {
        setError('Failed to load vendor approvals.');
        console.error('Error fetching vendor approvals:', err);
        // Handle unauthorized access
        if (err.response && err.response.status === 401) {
            router.push('/login');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchApprovals();
  }, [router]); // Add router to dependency array

  const handleApproval = async (id: string, status: 'approved' | 'rejected') => {
    try {
      await api.put(`/admin/vendor-approvals/${id}`, { status });
      setApprovals(approvals.map(app => (app.id === id ? { ...app, status } : app)));
      alert(`Vendor ${status === 'approved' ? 'approved' : 'rejected'} successfully.`);
    } catch (err) {
      setError(`Failed to ${status} vendor. Please try again.`);
      console.error(`Error ${status} vendor:`, err);
       if (err.response && err.response.status === 401) {
            router.push('/login');
        }
    }
  };

  if (loading) {
    return <div className="text-center p-4">Loading vendor approvals...</div>;
  }

  if (error) {
    return <div className="text-center p-4 text-red-500">{error}</div>;
  }

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4 text-center">Vendor Approval Queue</h1>
      {approvals.length === 0 ? (
        <p className="text-center">No pending vendor approvals.</p>
      ) : (
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Vendor Name</th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Applied At</th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {approvals.map((approval) => (
                <tr key={approval.id}>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{approval.vendorName}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{approval.email}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm">
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                      approval.status === 'approved' ? 'bg-green-100 text-green-800' :
                      approval.status === 'rejected' ? 'bg-red-100 text-red-800' :
                      'bg-yellow-100 text-yellow-800'
                    }`}>
                      {approval.status.charAt(0).toUpperCase() + approval.status.slice(1)}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{new Date(approval.appliedAt).toLocaleDateString()}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
                    {approval.status === 'pending' && (
                      <>
                        <button
                          onClick={() => handleApproval(approval.id, 'approved')}
                          className="text-green-600 hover:text-green-900 focus:outline-none focus:ring-2 focus:ring-green-500 rounded"
                        >
                          Approve
                        </button>
                        <button
                          onClick={() => handleApproval(approval.id, 'rejected')}
                          className="text-red-600 hover:text-red-900 focus:outline-none focus:ring-2 focus:ring-red-500 rounded"
                        >
                          Reject
                        </button>
                      </>
                    )}
                     {approval.status !== 'pending' && (
                        <p className="text-gray-400">No actions</p>
                     )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
