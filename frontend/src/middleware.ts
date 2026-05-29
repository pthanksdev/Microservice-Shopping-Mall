import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';
import { jwtDecode } from 'jwt-decode';

export function middleware(request: NextRequest) {
  const token = request.cookies.get('accessToken')?.value;
  const path = request.nextUrl.pathname;

  if (!token && (path.startsWith('/admin') || path.startsWith('/vendor') || path.startsWith('/account'))) {
    return NextResponse.redirect(new URL('/login', request.url));
  }

  if (token) {
    try {
      const decoded: any = jwtDecode(token);
      const role = decoded.role; // CUSTOMER, VENDOR, ADMIN

      if (path.startsWith('/admin') && role !== 'ADMIN') {
        return NextResponse.redirect(new URL('/unauthorized', request.url));
      }
      if (path.startsWith('/vendor') && role !== 'VENDOR' && role !== 'ADMIN') {
        return NextResponse.redirect(new URL('/unauthorized', request.url));
      }
    } catch (e) {
      // Invalid token
      return NextResponse.redirect(new URL('/login', request.url));
    }
  }

  return NextResponse.next();
}

export const config = {
  matcher: ['/admin/:path*', '/vendor/:path*', '/account/:path*'],
};
