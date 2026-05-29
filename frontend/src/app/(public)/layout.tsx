import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Shopping Mall",
  description: "A modern e-commerce platform",
};

export default function PublicLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div>
      {/* Add Navbar and Footer here */}
      <main>{children}</main>
    </div>
  );
}
