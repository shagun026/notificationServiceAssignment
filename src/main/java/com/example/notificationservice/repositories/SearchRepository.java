package com.example.notificationservice.repositories;

import com.example.notificationservice.entities.SearchEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


import java.time.LocalDateTime;
import java.util.List;

public interface SearchRepository extends ElasticsearchRepository<SearchEntity, Integer> {
    Page<SearchEntity> findByPhoneNumberAndCreatedAtBetweenOrderByCreatedAtDesc(String phoneNumber,
                                                                                LocalDateTime startTime,
                                                                                LocalDateTime endTime, Pageable pageable);

    Page<SearchEntity> findByMessage(String text, Pageable pageable);
}
