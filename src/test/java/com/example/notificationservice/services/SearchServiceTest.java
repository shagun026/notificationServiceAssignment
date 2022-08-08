package com.example.notificationservice.services;

import com.example.notificationservice.entities.SearchEntity;
import com.example.notificationservice.models.RequestBody.SearchRequestBody;
import com.example.notificationservice.repositories.SearchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private SearchRepository searchRepository;

    @InjectMocks
    private SearchService underTest;

    @Test
    void searchPhoneNumberWithinTimeRange() {
        SearchRequestBody request = SearchRequestBody.builder()
                .phoneNumber("+919876543210")
                .startTime("2022-07-04 12:12:12")
                .endTime("2022-12-12 12:12:12")
                .build();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(request.getStartTime(), formatter);
        LocalDateTime endTime = LocalDateTime.parse(request.getEndTime(), formatter);

        List<SearchEntity> entities = new ArrayList<>();

        Page<SearchEntity> page = new PageImpl<>(entities);
        Mockito.when(searchRepository.findByPhoneNumberAndCreatedAtBetweenOrderByCreatedAtDesc(
                request.getPhoneNumber(), startTime, endTime, PageRequest.of(0,50))).thenReturn(page);
        underTest.searchPhoneNumberWithinTimeRange(request);
        Mockito.verify(searchRepository).findByPhoneNumberAndCreatedAtBetweenOrderByCreatedAtDesc(request.getPhoneNumber(),
                startTime,
                endTime,
                PageRequest.of(0,50));
    }

    @Test
    void searchMessage() {
        List<SearchEntity> entities = new ArrayList<>();

        Page<SearchEntity> page = new PageImpl<>(entities);
        Mockito.when(searchRepository.findByMessageContaining("hello", PageRequest.of(0,50))).thenReturn(page);
        underTest.searchMessage("hello");
        Mockito.verify(searchRepository).findByMessageContaining("hello", PageRequest.of(0,50));
    }
}