'use client';
import Cart from "@/components/cart/Cart";

export const dynamic = 'force-dynamic';

const CartPage = () => {
  return (
    <div className="container mx-auto px-4 py-8">
      <h2 className="text-3xl font-bold mb-6">My Cart</h2>
      <Cart />
    </div>
  );
};

export default CartPage;
