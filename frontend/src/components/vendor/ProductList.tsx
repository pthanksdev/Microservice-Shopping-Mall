import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '@/lib/api';
import { Product } from '@/types/models';

const fetchVendorProducts = async () => {
  const { data } = await api.get('/products/vendor');
  return data;
};

const ProductList = () => {
  const queryClient = useQueryClient();
  const { data: products, isLoading, error } = useQuery<Product[]>({ 
    queryKey: ['vendorProducts'], 
    queryFn: fetchVendorProducts 
  });

  const deleteProduct = useMutation({
    mutationFn: (productId: string) => api.delete(`/products/${productId}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['vendorProducts'] });
    },
  });

  if (isLoading) return <div>Loading...</div>;
  if (error) return <div>Error loading products</div>;

  return (
    <ul className="space-y-4">
      {products?.map((product) => (
        <li key={product.id} className="p-4 border rounded-md flex justify-between items-center">
          <div>
            <h4 className="font-bold">{product.name}</h4>
            <p>${product.price.toFixed(2)}</p>
          </div>
          <button 
            onClick={() => deleteProduct.mutate(product.id)}
            className="bg-red-500 text-white px-3 py-1 rounded"
          >
            Delete
          </button>
        </li>
      ))}
    </ul>
  );
};

export default ProductList;
