'use client';

import type { Metadata } from "next";
import { Inter } from 'next/font/google';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import './globals.css';

const queryClient = new QueryClient();

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
        <QueryClientProvider client={queryClient}>
          <div>
            {children}
          </div>
        </QueryClientProvider>
      </body>
    </html>
  );
}
