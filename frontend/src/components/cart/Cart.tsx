import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '@/lib/api';
import { CartItem } from '@/types/models';

const fetchCart = async () => {
  const { data } = await api.get('/cart');
  return data;
};

const Cart = () => {
  const queryClient = useQueryClient();
  const { data: cart, isLoading, error } = useQuery<CartItem[]>({ queryKey: ['cart'], queryFn: fetchCart });

  const removeFromCart = useMutation({
    mutationFn: (productId: string) => api.post('/cart/remove', { productId }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cart'] });
    },
  });

  const checkout = useMutation({
    mutationFn: () => api.post('/orders/checkout'),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cart'] });
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      // Ideally, redirect to an order confirmation page
    },
  });

  if (isLoading) return <div>Loading cart...</div>;
  if (error) return <div>Error loading cart</div>;

  const totalPrice = cart?.reduce((acc, item) => acc + item.product.price * item.quantity, 0) || 0;

  return (
    <div>
      {cart?.length === 0 ? (
        <p>Your cart is empty.</p>
      ) : (
        <div className="space-y-4">
          {cart?.map((item) => (
            <div key={item.id} className="flex justify-between items-center p-4 border rounded-md">
              <div>
                <h4 className="font-bold">{item.product.name}</h4>
                <p>Quantity: {item.quantity}</p>
              </div>
              <div className="flex items-center">
                <span className="mr-4 font-bold">${(item.product.price * item.quantity).toFixed(2)}</span>
                <button 
                  onClick={() => removeFromCart.mutate(item.product.id)}
                  className="bg-red-500 text-white px-3 py-1 rounded"
                >
                  Remove
                </button>
              </div>
            </div>
          ))}
          <div className="text-right font-bold text-xl mt-4">
            Total: ${totalPrice.toFixed(2)}
          </div>
          <div className="text-right mt-4">
            <button 
              onClick={() => checkout.mutate()}
              className="bg-green-500 text-white px-6 py-2 rounded-md"
              disabled={checkout.isPending}
            >
              {checkout.isPending ? 'Processing...' : 'Checkout'}
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Cart;
