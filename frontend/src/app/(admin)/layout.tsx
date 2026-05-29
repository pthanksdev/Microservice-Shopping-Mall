'use client';
import { useAuthStore } from "@/store/useAuthStore";
import { useRouter } from "next/navigation";
import { useEffect } from "react";
import { Role } from "@/types/models";

function AdminLayout({ children }: { children: React.ReactNode }) {
  const { user, accessToken } = useAuthStore();
  const router = useRouter();

  useEffect(() => {
    if (!accessToken) {
      router.push("/login");
      return;
    }

    if (!user || user.role !== Role.ADMIN) {
      router.push("/unauthorized");
    }
  }, [user, accessToken, router]);

  if (!user || user.role !== Role.ADMIN) {
    return null; // Or a loading spinner
  }

  return (
    <div>
      {/* Add Admin-specific Sidebar/Navbar */}
      <main>{children}</main>
    </div>
  );
}

export default AdminLayout;
