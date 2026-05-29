import { useQuery } from '@tanstack/react-query';
import { api } from '@/lib/api';
import { Order } from '@/types/models';

const fetchOrders = async () => {
  const { data } = await api.get('/orders');
  return data;
};

const UserOrders = () => {
  const { data: orders, isLoading, error } = useQuery<Order[]>({ queryKey: ['orders'], queryFn: fetchOrders });

  if (isLoading) return <div>Loading orders...</div>;
  if (error) return <div>Error loading orders</div>;

  return (
    <div className="mt-8">
      <h3 className="text-lg font-bold">My Orders</h3>
      {orders?.length === 0 ? (
        <p>You have no orders.</p>
      ) : (
        <ul className="space-y-4">
          {orders?.map((order) => (
            <li key={order.id} className="p-4 border rounded-md">
              <div className="flex justify-between">
                <span>Order #{order.id}</span>
                <span>{new Date(order.createdAt).toLocaleDateString()}</span>
              </div>
              <div>
                {order.items.map((item) => (
                  <div key={item.id} className="flex justify-between">
                    <span>{item.product.name} x {item.quantity}</span>
                    <span>${(item.price * item.quantity).toFixed(2)}</span>
                  </div>
                ))}
              </div>
              <div className="text-right font-bold mt-2">
                Total: ${order.items.reduce((acc, item) => acc + item.price * item.quantity, 0).toFixed(2)}
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default UserOrders;
