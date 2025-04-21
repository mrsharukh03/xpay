package com.xpay.Anotations;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RateLimitResponse {
    private boolean allowed;
    private long retryAfterSeconds;

    public RateLimitResponse(boolean allowed, long retryAfterSeconds) {
        this.allowed = allowed;
        this.retryAfterSeconds = retryAfterSeconds;
    }
}

