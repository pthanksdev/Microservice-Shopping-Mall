'use client';
import { User, Role } from "@/types/models";
import { useAuthStore } from "@/store/useAuthStore";
import { useRouter } from "next/navigation";
import React, { useEffect, useState } from "react";

export function protectRoute<P extends object>(
  Component: React.ComponentType<P>,
  allowedRoles: Role[]
) {
  return function ProtectedComponent(props: P) {
    const router = useRouter();
    const { user, accessToken } = useAuthStore();
    const [isSSR, setIsSSR] = useState(true);

    useEffect(() => {
        setIsSSR(false);
    }, []);

    useEffect(() => {
      if (!accessToken && !isSSR) {
        router.push("/login");
        return;
      }

      if ((!user || !allowedRoles.includes(user.role)) && !isSSR) {
        router.push("/unauthorized");
      }
    }, [user, accessToken, router, isSSR]);

    if (isSSR || !user || !allowedRoles.includes(user.role)) {
      return null; // Or a loading spinner
    }

    return <Component {...props} />;
  };
}
