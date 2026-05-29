import { useQuery } from '@tanstack/react-query';
import { api } from '@/lib/api';
import { Product } from '@/types/models';
import ProductCard from './ProductCard';

const fetchProducts = async () => {
  const { data } = await api.get('/products');
  return data;
};

const ProductGrid = () => {
  const { data: products, isLoading, error } = useQuery<Product[]>({ queryKey: ['products'], queryFn: fetchProducts });

  if (isLoading) return <div>Loading products...</div>;
  if (error) return <div>Error loading products</div>;

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
      {products?.map((product) => (
        <ProductCard key={product.id} product={product} />
      ))}
    </div>
  );
};

export default ProductGrid;
