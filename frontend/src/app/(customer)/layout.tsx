import { protectRoute } from "@/lib/protectRoute";

function CustomerLayout({ children }: { children: React.ReactNode }) {
  return (
    <div>
      {/* Add Customer-specific Sidebar/Navbar */}
      <main>{children}</main>
    </div>
  );
}

export default protectRoute(CustomerLayout, ["CUSTOMER"]);
