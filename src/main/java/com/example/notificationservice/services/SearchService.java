package com.example.notificationservice.services;

import com.example.notificationservice.entities.SearchEntity;
import com.example.notificationservice.models.RequestBody.SearchPhoneNumberWithinTime;
import com.example.notificationservice.repositories.SearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SearchService {

    private SearchRepository searchRepository;

    public SearchService(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    public void indexDetails(SearchEntity searchEntity){
        searchRepository.save(searchEntity);
    }

    public SearchEntity findRequest(Integer id){
        return searchRepository.findById(id).orElse(null);
    }

    public List<SearchEntity> searchPhoneNumberWithinTimeRange(SearchPhoneNumberWithinTime request){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(request.getStartTime(), formatter);
        LocalDateTime endTime = LocalDateTime.parse(request.getEndTime(), formatter);

        Page<SearchEntity> searchEntityPage = searchRepository.findByPhoneNumberAndCreatedAtBetweenOrderByCreatedAtDesc(request.getPhoneNumber(),
                startTime,
                endTime,
                PageRequest.of(0,50));

        return searchEntityPage.getContent();
    }

    public List<SearchEntity> searchMessage(String text){
        Page<SearchEntity> searchEntityPage = searchRepository.findByMessage(text,
                PageRequest.of(0,50));
        return searchEntityPage.getContent();
    }
}
