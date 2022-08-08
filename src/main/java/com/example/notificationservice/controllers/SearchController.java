package com.example.notificationservice.controllers;

import com.example.notificationservice.entities.SearchEntity;
import com.example.notificationservice.models.RequestBody.SearchRequestBody;
import com.example.notificationservice.models.ResponseBody.SearchResponse;
import com.example.notificationservice.services.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/sms/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("{id}")
    public SearchEntity findRequest(@PathVariable Integer id){
        return searchService.findRequest(id);
    }

    @GetMapping("withinTimeRange")
    public ResponseEntity<SearchResponse> searchPhoneNumberWithinTimeRange(@RequestBody SearchRequestBody request){
        List<SearchEntity> result = searchService.searchPhoneNumberWithinTimeRange(request);
        SearchResponse response = SearchResponse.builder().searchResponse(result).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("messages")
    public ResponseEntity<SearchResponse> searchMessage(@RequestBody SearchRequestBody request){
        List<SearchEntity> result = searchService.searchMessage(request.getMessage());
        SearchResponse response = SearchResponse.builder().searchResponse(result).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
