package com.xpay.Anotations;

import com.xpay.Services.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private RateLimiterService rateLimiterService;

    @Around("@annotation(com.xpay.Anotations.RateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        RateLimit rateLimit = method.getAnnotation(RateLimit.class);
        int limit = rateLimit.limit();
        int window = rateLimit.window();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String path  = request.getRequestURI();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RateLimitExceededException("User not authenticated");
        }
        String username = authentication.getName();

        String redisKey = "ratelimit:" + username + ":" + path;
        RateLimitResponse rateLimitResponse = rateLimiterService.isAllowed(redisKey, limit, window);

        if (!rateLimitResponse.isAllowed()) {
            String message = String.format(
                    "Rate limit exceeded on [%s]. Limit: %d requests per %d seconds. Try again after %d seconds.",
                    path, limit, window, rateLimitResponse.getRetryAfterSeconds()
            );
            throw new RateLimitExceededException(message);
        }

        return joinPoint.proceed();
    }
    // Custom exception for rate limit exceeded
    public static class RateLimitExceededException extends RuntimeException {
        public RateLimitExceededException(String msg) {
            super(msg);
        }
    }
}
