package com.example.Ecommerce_YV.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Tempat daftarin si AdminInterceptor ke Spring biar dia tau mana aja URL yang harus dijaga.
// Kalau class ini nggak ada, interceptornya udah dibuat tapi nggak dipasang kemana-mana.
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor)
                // Semua yang masuk ke bawah /api/dashboard wajib lewat penjaga dulu
                .addPathPatterns("/api/dashboard/**");
    }
}
