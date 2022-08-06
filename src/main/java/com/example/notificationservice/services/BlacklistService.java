package com.example.notificationservice.services;

import com.example.notificationservice.entities.Blacklist;
import com.example.notificationservice.repositories.BlacklistCacheManager;
import com.example.notificationservice.repositories.BlacklistRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BlacklistService {


    private final BlacklistRepository blacklistRepository;
    private final BlacklistCacheManager blacklistCacheManager;

    public BlacklistService(BlacklistRepository blacklistRepository, BlacklistCacheManager blacklistCacheManager) {
        this.blacklistRepository = blacklistRepository;
        this.blacklistCacheManager = blacklistCacheManager;
    }

    public List<String> getBlacklist() {
        Set<String> keys = blacklistCacheManager.getAllKeys();
        List<String> phoneNumbers = keys
                .stream()
                .collect(Collectors.toList());
        return phoneNumbers;
    }

    public void addToBlacklist(List<String> phoneNumbers) {
        for (String number : phoneNumbers) {
            blacklistCacheManager.addToBlacklist(number);

            blacklistRepository.save(
                    Blacklist
                            .builder()
                            .phoneNumber(number)
                            .isPresent(Boolean.TRUE)
                            .build());
        }

    }

    public boolean isBlacklisted(String phoneNumber) {
        return blacklistCacheManager.isBlacklisted(phoneNumber);
    }
    public void removeFromBlacklist(List<String> phoneNumbers) {
        List<String> presentNumbers = phoneNumbers
                .stream()
                .filter(blacklistCacheManager::isBlacklisted)
                .collect(Collectors.toList());

        blacklistCacheManager.removeFromBlacklist(phoneNumbers);
        blacklistRepository.deleteAllById(presentNumbers);
    }
}