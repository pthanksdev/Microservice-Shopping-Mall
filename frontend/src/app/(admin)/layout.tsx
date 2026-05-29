import { protectRoute } from "@/lib/protectRoute";

function AdminLayout({ children }: { children: React.ReactNode }) {
  return (
    <div>
      {/* Add Admin-specific Sidebar/Navbar */}
      <main>{children}</main>
    </div>
  );
}

export default protectRoute(AdminLayout, ["ADMIN"]);
