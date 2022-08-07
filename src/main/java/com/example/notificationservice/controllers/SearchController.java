package com.example.notificationservice.controllers;

import com.example.notificationservice.entities.SearchEntity;
import com.example.notificationservice.models.RequestBody.SearchPhoneNumberWithinTime;
import com.example.notificationservice.services.SearchService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("v1/sms/search")
public class SearchController {

    private SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }


//    @PostMapping
//    public void indexDetails(@RequestBody SearchEntity msg){
//        searchService.indexDetails(msg);
//    }

    @GetMapping("{id}")
    public SearchEntity findRequest(@PathVariable Integer id){
        SearchEntity msg = searchService.findRequest(id);
        return msg;
    }

    @GetMapping("withinTimeRange")
    public List<SearchEntity> searchPhoneNumberWithinTimeRange(@RequestBody SearchPhoneNumberWithinTime request){

        return searchService.searchPhoneNumberWithinTimeRange(request);
    }

    @GetMapping("messages")
    public List<SearchEntity> searchMessage(@RequestBody String text){
        return searchService.searchMessage(text);
    }
}
