import { protectRoute } from "@/lib/protectRoute";

function VendorLayout({ children }: { children: React.ReactNode }) {
  return (
    <div>
      {/* Add Vendor-specific Sidebar/Navbar */}
      <main>{children}</main>
    </div>
  );
}

export default protectRoute(VendorLayout, ["VENDOR", "ADMIN"]);
