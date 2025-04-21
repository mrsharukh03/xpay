package com.xpay.Security.AllowOrigins;

import com.xpay.Entitys.Client.Client;
import com.xpay.Reposititorys.ClientRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AllowedOriginsCache {

    @Autowired
    private ClientRepo clientRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY = "allowed_origins";

    @PostConstruct
    public void loadAllowedOrigins() {
        refreshAllowedOrigins();
    }

    public void refreshAllowedOrigins() {
        List<String> allowedOrigins = clientRepository.findAll().stream()
                .map(client -> client.getWebsiteUrl().trim())  // Trim the URLs
                .collect(Collectors.toList());

        redisTemplate.delete(CACHE_KEY);  // Remove previous cache
        // Push each individual origin to Redis as a string
        allowedOrigins.forEach(origin -> redisTemplate.opsForList().rightPush(CACHE_KEY, origin));  // Push each origin as a string
    }



    public boolean isAllowedOrigin(String origin) {
        String trimmedOrigin = origin.trim();
        // Get the list of allowed origins from Redis
        List<Object> cachedOrigins = redisTemplate.opsForList().range(CACHE_KEY, 0, -1);
        boolean isContains = cachedOrigins.stream()
                .map(Object::toString) // Convert Object to String
                .map(String::trim)     // Trim spaces for each origin
                .anyMatch(allowedOrigin -> allowedOrigin.equals(trimmedOrigin));  // Compare to trimmed origin
        return isContains;
    }

    public boolean isValidApiKey(String apiKey,String origen) {
        Client client = clientRepository.findByApiKey(apiKey);
        if(client == null) return false;
        return client.getWebsiteUrl().equals(origen);
    }
}
