package com.example.notificationservice.repositories;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class BlacklistCacheManager {
    private final RedisTemplate<String,Boolean> redisTemplate;
    private final ValueOperations valueOperations;

    public BlacklistCacheManager(RedisTemplate<String, Boolean> redisTemplate) {
        this.redisTemplate = redisTemplate;
        valueOperations = redisTemplate.opsForValue();
    }

    public Set<String> getAllKeys(){
        return redisTemplate.keys("*");
    }

    public boolean isBlacklisted(String phoneNumber) {
        return valueOperations.get(phoneNumber) != null;
    }

    public void removeFromBlacklist(List<String> phoneNumbers)
    {
        redisTemplate.delete(phoneNumbers);
    }

    public void addToBlacklist(String number){
        valueOperations.setIfAbsent(number, Boolean.TRUE);
    }

}
