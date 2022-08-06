package com.example.notificationservice.controllers;

import com.example.notificationservice.models.RequestBody.BlacklistRequestBody;
import com.example.notificationservice.models.ResponseBody.GenericResponse;
import com.example.notificationservice.services.BlacklistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "v1/blacklist")
public class BlacklistController {
    private final BlacklistService blacklistService;

    public BlacklistController(BlacklistService blacklistService) {
        this.blacklistService = blacklistService;
    }

    @GetMapping
    public ResponseEntity<GenericResponse> getBlacklistedNumbers(){
        List<String> data =  blacklistService.getBlacklist();
        return ResponseEntity.ok(GenericResponse.builder().data(data).build());
    }

    @PostMapping
    public ResponseEntity<GenericResponse> addNumberToBlacklist(
            @RequestBody BlacklistRequestBody blacklistRequestBody){
        List<String> phoneNumbers = blacklistRequestBody.getNumbers();
        blacklistService.addToBlacklist(phoneNumbers);
        return ResponseEntity.ok(GenericResponse.builder().data("Successfully Blacklisted").build());
    }

    @GetMapping(path = "{phoneNumber}")
    public boolean checkIfBlacklisted(@PathVariable("phoneNumber") String phoneNumber){
        return blacklistService.isBlacklisted(phoneNumber);
    }


    @DeleteMapping
    public ResponseEntity<GenericResponse> deleteNumberFromBlacklist(
            @RequestBody BlacklistRequestBody blacklistRequestBody){
        List<String> phoneNumbers = blacklistRequestBody.getNumbers();
        blacklistService.removeFromBlacklist(phoneNumbers);
        return ResponseEntity.ok(GenericResponse.builder().data("Successfully Whitelisted").build());
    }
}