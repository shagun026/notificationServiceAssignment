package com.example.notificationservice.services;

import com.example.notificationservice.entities.Blacklist;
import com.example.notificationservice.repositories.BlacklistCacheManager;
import com.example.notificationservice.repositories.BlacklistRepository;
import org.apache.kafka.common.protocol.types.Field;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BlacklistServiceTest {

    @Mock
    private BlacklistRepository blacklistRepository;

    @Mock
    private BlacklistCacheManager blacklistCacheManager;

    @Mock
    private RedisTemplate<String,Boolean> redisTemplate;

    @Mock
    private ValueOperations valueOperations;
    @InjectMocks
    private BlacklistService underTest;

    @Test
    void getBlacklistTest() {

        Set<String> s = new HashSet<>();
        s.add("1");
        Mockito.when(blacklistCacheManager.getAllKeys()).thenReturn(s);
        underTest.getBlacklist();
        Mockito.verify(blacklistCacheManager).getAllKeys();
    }

    @Test
    void addToBlacklistTest() {

        List<String> samplePhoneNumbers = new ArrayList<>();
        String samplePhoneNumber = "+919876543210";
        samplePhoneNumbers.add(samplePhoneNumber);

        underTest.addToBlacklist(samplePhoneNumbers);

        //test blacklist repository
        Blacklist blacklistNumber = Blacklist.builder().phoneNumber(samplePhoneNumber).isPresent(Boolean.TRUE).build();

        ArgumentCaptor<Blacklist> blacklistCaptor = ArgumentCaptor.forClass(Blacklist.class);
        Mockito.verify(blacklistRepository).save(blacklistCaptor.capture());

        Blacklist capturedBlacklistNumber = blacklistCaptor.getValue();

        assertThat(capturedBlacklistNumber).isEqualTo(blacklistNumber);

        //test redis cache
        ArgumentCaptor<String> blacklistCacheArgumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(blacklistCacheManager).addToBlacklist(blacklistCacheArgumentCaptor.capture());
        String capturedNumber = blacklistCacheArgumentCaptor.getValue();
        assertThat(capturedNumber).isEqualTo(samplePhoneNumber);
    }

    @Test
    void isBlacklistedTest(){
        String phoneNumber = "+919876543210";
        Mockito.when(blacklistCacheManager.isBlacklisted(phoneNumber)).thenReturn(Boolean.TRUE);
        underTest.isBlacklisted(phoneNumber);
        verify(blacklistCacheManager, times(1)).isBlacklisted(phoneNumber);
    }

    @Test
    void removeFromBlacklistTest() {
        List<String> samplePhoneNumbers = new ArrayList<>();
        String samplePhoneNumber = "+919876543210";
        samplePhoneNumbers.add(samplePhoneNumber);

        Mockito.when(blacklistCacheManager.isBlacklisted(ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
        underTest.removeFromBlacklist(samplePhoneNumbers);

        Mockito.verify(blacklistRepository, times(1)).deleteAllById(samplePhoneNumbers);
        Mockito.verify(blacklistCacheManager, times(1)).removeFromBlacklist(samplePhoneNumbers);
    }
}