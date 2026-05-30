import type { Metadata } from "next";
import './globals.css';
import PublicLayoutClient from './PublicLayoutClient';
import { Inter } from 'next/font/google';

const inter = Inter({ subsets: ['latin'] });

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
      <body className={inter.className}>
        <PublicLayoutClient>
          {children}
        </PublicLayoutClient>
      </body>
    </html>
  );
}
