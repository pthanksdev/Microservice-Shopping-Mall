import { Product } from "@/types/models";
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '@/lib/api';

interface ProductCardProps {
  product: Product;
}

const ProductCard = ({ product }: ProductCardProps) => {
  const queryClient = useQueryClient();

  const addToCart = useMutation({
    mutationFn: () => api.post('/cart/add', { productId: product.id, quantity: 1 }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cart'] });
    },
  });

  return (
    <div className="border rounded-md overflow-hidden">
      <div className="p-4">
        <h3 className="text-lg font-bold">{product.name}</h3>
        <p className="text-gray-600">{product.description}</p>
        <div className="flex justify-between items-center mt-4">
          <span className="font-bold text-xl">${product.price.toFixed(2)}</span>
          <button 
            onClick={() => addToCart.mutate()}
            className="bg-blue-500 text-white px-4 py-2 rounded-md"
            disabled={addToCart.isPending}
          >
            {addToCart.isPending ? 'Adding...' : 'Add to Cart'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProductCard;
