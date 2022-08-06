package com.example.notificationservice.controllers;

import com.example.notificationservice.exceptions.BadRequestException;
import com.example.notificationservice.exceptions.NotFoundException;
import com.example.notificationservice.models.RequestBody.SmsRequestBody;
import com.example.notificationservice.models.ResponseBody.GenericResponse;
import com.example.notificationservice.entities.SmsRequest;
import com.example.notificationservice.services.NotificationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.notificationservice.models.ResponseBody.Error;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "v1/sms")
@Slf4j
public class NotificationsController {

    private final NotificationsService notificationService;

    public NotificationsController(NotificationsService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping(path = "send")
    public ResponseEntity<GenericResponse> sendSMS(@RequestBody SmsRequestBody smsRequestBody) {
        try {
            SmsRequest responseObject = notificationService.addNewSmsRequest(smsRequestBody);
            HashMap<String,String> data = new HashMap<>();
            data.put("request_id",responseObject.getId().toString());
            data.put("comments","Successfully Sent");
            return ResponseEntity.ok(GenericResponse.builder().data(data).build());
        }catch(BadRequestException e){
            log.error("Exception occurred : {} {}",e.getMessage(),e.getStackTrace());
            Error error = new Error();
            error.setCode("INVALID_REQUEST");
            error.setMessage("Phone number is mandatory");
            return ResponseEntity.badRequest().body(GenericResponse.builder().error(error).build());
        }
    }

    @GetMapping
    public List<SmsRequest> getAllSMS(){
        return notificationService.getSmsRequests();
    }

    @DeleteMapping(path = "{id}")
    public void deleteSmsRequest(@PathVariable Integer id){
        notificationService.deleteSmsRequest(id);
    }

    @GetMapping(path = "{requestId}")
    public ResponseEntity<GenericResponse> getSmsDetails(@PathVariable Integer requestId){
        Optional<SmsRequest> smsRequest = null;
        try{
            smsRequest =notificationService.getSmsDetails(requestId);

        }catch(NotFoundException e){
            log.error("Exception occurred : {} trace : {}",e.getMessage(),e.getStackTrace() );
            Error error = new Error("INVALID_REQUEST","request_id not found");
            return new ResponseEntity(GenericResponse.builder().error(error).build(), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(GenericResponse.builder().data(smsRequest).build());
    }
}
