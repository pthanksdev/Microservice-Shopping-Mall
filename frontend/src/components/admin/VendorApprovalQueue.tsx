import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '@/lib/api';
import { User } from '@/types/models';

const fetchPendingVendors = async () => {
  const { data } = await api.get('/admin/pending-vendors');
  return data;
};

const VendorApprovalQueue = () => {
  const queryClient = useQueryClient();
  const { data: vendors, isLoading, error } = useQuery<User[]>({ 
    queryKey: ['pendingVendors'], 
    queryFn: fetchPendingVendors 
  });

  const approveVendor = useMutation({
    mutationFn: (vendorId: string) => api.post(`/admin/approve-vendor/${vendorId}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pendingVendors'] });
    },
  });

  if (isLoading) return <div>Loading...</div>;
  if (error) return <div>Error loading vendors</div>;

  return (
    <div className="p-4 border rounded-md">
      <h3 className="text-lg font-bold mb-4">Vendor Approval Queue</h3>
      {vendors?.length === 0 ? (
        <p>No pending vendor applications.</p>
      ) : (
        <ul className="space-y-2">
          {vendors?.map((vendor) => (
            <li key={vendor.id} className="flex justify-between items-center p-2 border rounded">
              <span>{vendor.name} ({vendor.email})</span>
              <button 
                onClick={() => approveVendor.mutate(vendor.id)}
                className="bg-green-500 text-white px-3 py-1 rounded"
              >
                Approve
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default VendorApprovalQueue;
