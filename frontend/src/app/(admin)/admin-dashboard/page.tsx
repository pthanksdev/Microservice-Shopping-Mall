import { protectRoute } from "@/lib/protectRoute";
import { Role } from "@/types/models";
import AdminDashboard from "@/components/admin/AdminDashboard";

const AdminDashboardPage = () => {
  return <AdminDashboard />;
};

export default protectRoute(AdminDashboardPage, [Role.ADMIN]);
