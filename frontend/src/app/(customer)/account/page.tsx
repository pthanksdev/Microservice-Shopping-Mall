import { protectRoute } from "@/lib/protectRoute";
import { Role } from "@/types/models";
import UserInformation from "@/components/account/UserInformation";
import UserOrders from "@/components/account/UserOrders";

const CustomerAccountPage = () => {
  return (
    <div className="container mx-auto px-4 py-8">
      <h2 className="text-3xl font-bold mb-6">My Account</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div>
          <h3 className="text-2xl font-semibold mb-4">My Information</h3>
          <UserInformation />
        </div>
        <div>
          <h3 className="text-2xl font-semibold mb-4">My Orders</h3>
          <UserOrders />
        </div>
      </div>
    </div>
  );
};

export default protectRoute(CustomerAccountPage, [Role.CUSTOMER]);
