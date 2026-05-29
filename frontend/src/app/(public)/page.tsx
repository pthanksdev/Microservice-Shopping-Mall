import ProductGrid from "@/components/products/ProductGrid";

export const dynamic = 'force-dynamic';

const HomePage = () => {
  return (
    <div className="container mx-auto px-4">
      <h1 className="text-3xl font-bold my-6">Products</h1>
      <ProductGrid />
    </div>
  );
};

export default HomePage;
