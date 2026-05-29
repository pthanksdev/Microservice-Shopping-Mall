'use client';
import { useAuthStore } from "@/store/useAuthStore";
import { useRouter } from "next/navigation";
import { useEffect } from "react";
import { Role } from "@/types/models";

function CustomerLayout({ children }: { children: React.ReactNode }) {
  const { user, accessToken } = useAuthStore();
  const router = useRouter();

  useEffect(() => {
    if (!accessToken) {
      router.push("/login");
      return;
    }

    if (!user || user.role !== Role.CUSTOMER) {
      router.push("/unauthorized");
    }
  }, [user, accessToken, router]);

  if (!user || user.role !== Role.CUSTOMER) {
    return null; // Or a loading spinner
  }

  return (
    <div>
      {/* Add Customer-specific Sidebar/Navbar */}
      <main>{children}</main>
    </div>
  );
}

export default CustomerLayout;
