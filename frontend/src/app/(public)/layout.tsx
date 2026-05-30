import type { Metadata } from "next";
import './globals.css';
import PublicLayoutClient from './PublicLayoutClient';

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
    <html lang="en">
      <body>
        <PublicLayoutClient>
          {children}
        </PublicLayoutClient>
      </body>
    </html>
  );
}
