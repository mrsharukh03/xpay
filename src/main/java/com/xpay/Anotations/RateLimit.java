package com.xpay.Anotations;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    int limit();            // Max requests
    int window();           // Time window in seconds
}
