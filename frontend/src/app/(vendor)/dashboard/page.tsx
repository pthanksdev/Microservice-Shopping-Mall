import { protectRoute } from "@/lib/protectRoute";
import { Role } from "@/types/models";
import VendorDashboard from "@/components/vendor/VendorDashboard";

const VendorDashboardPage = () => {
  return <VendorDashboard />;
};

export default protectRoute(VendorDashboardPage, [Role.VENDOR]);
