'use client';

import { Inter } from 'next/font/google';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

const queryClient = new QueryClient();

const inter = Inter({ subsets: ['latin'] });

export default function PublicLayoutClient({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <body className={inter.className}>
      <QueryClientProvider client={queryClient}>
        {children}
      </QueryClientProvider>
    </body>
  );
}
