import { User, Role } from "@/types/models";
import { useAuthStore } from "@/store/useAuthStore";
import { useRouter } from "next/navigation";
import React, { useEffect } from "react";

export function protectRoute<P extends object>(
  Component: React.ComponentType<P>,
  allowedRoles: Role[]
) {
  return function ProtectedComponent(props: P) {
    const router = useRouter();
    const { user, accessToken } = useAuthStore();

    useEffect(() => {
      if (!accessToken) {
        router.push("/login");
        return;
      }

      if (!user || !allowedRoles.includes(user.role)) {
        router.push("/unauthorized");
      }
    }, [user, accessToken, router]);

    if (!user || !allowedRoles.includes(user.role)) {
      return null; // Or a loading spinner
    }

    return <Component {...props} />;
  };
}
