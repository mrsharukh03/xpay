package com.xpay.Security.AllowOrigins;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private WalletSecurityInterceptor walletSecurityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(walletSecurityInterceptor)
                .addPathPatterns("/api/wallet/**"); // Apply only to wallet APIs
    }
}
