package com.example.Ecommerce_YV.Security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// Penjaga pintu buat semua endpoint Dashboard.
// Setiap request yang masuk ke /api/dashboard/** akan dicegat di sini dulu.
// Kalau tidak ada bukti bahwa pengirimmnya adalah Admin, langsung ditolak mentah-mentah.
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // OPTIONS tetap diizinkan untuk keperluan CORS preflight
        String method = request.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        String role = request.getHeader("X-User-Role");
        String userId = request.getHeader("X-User-Id");

        // Butuh keduanya ada: userId dan role = ADMIN
        if (userId == null || userId.isBlank() || !"ADMIN".equalsIgnoreCase(role)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("Akses ditolak. Halaman ini khusus Admin.");
            return false;
        }

        return true;
    }
}
