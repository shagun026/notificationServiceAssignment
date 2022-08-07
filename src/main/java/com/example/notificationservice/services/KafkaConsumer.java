package com.example.notificationservice.services;

import com.example.notificationservice.entities.SearchEntity;
import com.example.notificationservice.entities.SmsRequest;
import com.example.notificationservice.models.RequestBody.ThirdPartyRequestBody;
import com.example.notificationservice.models.ResponseBody.ThirdPartyResponseBody;
import com.example.notificationservice.repositories.SmsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static com.example.notificationservice.constants.Constant.KEY;
import static com.example.notificationservice.constants.Constant.TOPIC;

@Service
@Slf4j
public class KafkaConsumer {

    @Autowired
    RestTemplate restTemplate;
    private final NotificationsService notificationService;
    private final BlacklistService blacklistService;
    private final SmsRepository smsRepository;
    private final SearchService searchService;

    @Autowired
    public KafkaConsumer(
            NotificationsService notificationService, BlacklistService blacklistService, SmsRepository smsRepository,
            SearchService searchService) {
        this.notificationService = notificationService;
        this.blacklistService = blacklistService;
        this.smsRepository = smsRepository;
        this.searchService = searchService;
    }

    @KafkaListener(topics = TOPIC, groupId = "groupId")
    private void consume(Integer requestId) {
        Optional<SmsRequest> optionalSmsRequest = notificationService.getSmsDetails(requestId);

        SmsRequest smsRequest = optionalSmsRequest.get();
        boolean isBlacklisted = blacklistService.isBlacklisted(smsRequest.getPhoneNumber());

        ArrayList<ThirdPartyRequestBody> requestBody = new ArrayList<>();

        ThirdPartyRequestBody thirdPartyRequestBody = buildRequestBody(smsRequest);
        requestBody.add(thirdPartyRequestBody);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("key", KEY);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<ArrayList<ThirdPartyRequestBody>> request = new HttpEntity<>(requestBody, headers);

        if (!isBlacklisted) {

            ThirdPartyResponseBody thirdPartyResponseBody = restTemplate
                    .exchange("https://api.imiconnect.in/resources/v1/messaging", HttpMethod.POST,
                            request, ThirdPartyResponseBody.class).getBody();
            if (thirdPartyResponseBody.getResponse().get(0).getCode().equals("1001")) {
                smsRequest.setStatus("SUCCESS");
                smsRepository.save(smsRequest);
            } else {
                smsRequest.setStatus("FAILURE");
                smsRepository.save(smsRequest);
            }

            SearchEntity searchEntity = SearchEntity
                    .builder()
                    .id(smsRequest.getId())
                    .phoneNumber(smsRequest.getPhoneNumber())
                    .message(smsRequest.getMessage())
                    .status(smsRequest.getStatus())
                    .createdAt(LocalDateTime.now())
                    .build();

            searchService.indexDetails(searchEntity);
        }

    }

    private ThirdPartyRequestBody buildRequestBody(SmsRequest smsRequest) {

        ArrayList<String> msisdn = new ArrayList<>();
        msisdn.add(smsRequest.getPhoneNumber());

        ArrayList<ThirdPartyRequestBody.Destination> destinations = new ArrayList<>();
        destinations.add(ThirdPartyRequestBody.Destination.builder()
                .msisdn(msisdn)
                .correlationId(smsRequest.getId().toString())
                .build());

        ThirdPartyRequestBody body = ThirdPartyRequestBody
                .builder()
                .deliveryChannel("sms")
                .channels(ThirdPartyRequestBody.Channel.builder()
                        .sms(ThirdPartyRequestBody.Channel.Sms.builder().text(smsRequest.getMessage()).build())
                        .build())
                .destination(destinations)
                .build();
        return body;
    }
}
