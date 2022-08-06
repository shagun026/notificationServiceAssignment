package com.example.notificationservice.services;

import com.example.notificationservice.entities.SmsRequest;
import com.example.notificationservice.exceptions.BadRequestException;
import com.example.notificationservice.exceptions.NotFoundException;
import com.example.notificationservice.models.RequestBody.SmsRequestBody;
import com.example.notificationservice.repositories.SmsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.notificationservice.constants.Constant.TOPIC;

@Service
@Slf4j
public class NotificationsService {


    private final SmsRepository smsRepository;

    private KafkaTemplate kafkaTemplate;

    @Autowired
    public NotificationsService(SmsRepository smsRepository, KafkaTemplate kafkaTemplate) {
        this.smsRepository = smsRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     *
     * @param smsRequestBody
     * @return
     * @throws BadRequestException
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public SmsRequest addNewSmsRequest(SmsRequestBody smsRequestBody) throws BadRequestException {
        validateSmsRequest(smsRequestBody);
        SmsRequest smsRequest = SmsRequest.builder()
                .phoneNumber(smsRequestBody.getPhoneNumber())
                .message(smsRequestBody.getMessage())
                .build();

        smsRepository.save(smsRequest);
        kafkaTemplate.send(TOPIC,String.valueOf(smsRequest.getId()));
        return smsRequest;
    }

    private void validateSmsRequest(SmsRequestBody smsRequest) {
        if(smsRequest.getPhoneNumber().isEmpty()){
            throw new BadRequestException("Phone number can not be empty!");
        }
        else if(!checkPhoneNumber(smsRequest.getPhoneNumber())){
            throw new BadRequestException("Phone number is invalid!");
        }
        else if(smsRequest.getMessage().isEmpty()){
            throw new BadRequestException("Message can not be empty!");
        }
    }

    private boolean checkPhoneNumber(String number){
        Pattern pattern = Pattern
                .compile("\\+91[6-9][0-9]{9}");
        Matcher matcher = pattern.matcher(number);
        return (matcher.find() && matcher.group().equals(number));
    }


    public List<SmsRequest> getSmsRequests() {
        return smsRepository.findAll();
    }

    public void deleteSmsRequest(Integer id) {
        if (smsRepository.existsById(id)) {
            smsRepository.deleteById(id);
        }
    }

    public Optional<SmsRequest> getSmsDetails(Integer id) throws NotFoundException {
        Optional<SmsRequest> smsRequestOptional = smsRepository.findById(id);
        if(!smsRequestOptional.isPresent()){
            throw new NotFoundException("Request id not found!");
        }
        return smsRequestOptional;
    }
}
