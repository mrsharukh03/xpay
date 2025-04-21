package com.xpay.Security.AllowOrigins;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class WalletSecurityInterceptor implements HandlerInterceptor {

    @Autowired
    private AllowedOriginsCache allowedOriginsCache;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String origin = request.getHeader("Origin");
        String apiKey = request.getHeader("x-api-key");

        if (origin != null && apiKey != null && allowedOriginsCache.isAllowedOrigin(origin)) {
            if (!allowedOriginsCache.isValidApiKey(apiKey,origin)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Unauthorized Access - Invalid API Key");
                return false;
            }
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT,DELETE");
            response.setHeader("Access-Control-Allow-Credentials", "true");
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Unauthorized Access - Invalid Origin or api");
            return false;
        }

        return true;
    }
}

