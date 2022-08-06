package com.example.notificationservice.services;

import com.example.notificationservice.entities.SmsRequest;
import com.example.notificationservice.exceptions.BadRequestException;
import com.example.notificationservice.exceptions.NotFoundException;
import com.example.notificationservice.models.RequestBody.SmsRequestBody;
import com.example.notificationservice.repositories.SmsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class NotificationsServiceTest {

    @Mock
    private SmsRepository smsRepository;

    @Mock
    private KafkaTemplate kafkaTemplate;
    @InjectMocks
    private NotificationsService underTest;

    @Test
    void getSmsRequestsTest() {
        //when
        underTest.getSmsRequests();

        //then
        Mockito.verify(smsRepository).findAll();
    }

    @Test
    void addNewSmsRequestTest() {
        //given
        SmsRequestBody smsRequestBody = SmsRequestBody.builder().phoneNumber("+919876543210").message("hello").build();

        //when
        underTest.addNewSmsRequest(smsRequestBody);
        SmsRequest smsRequest = SmsRequest.builder().id(0).phoneNumber(smsRequestBody.getPhoneNumber()).message(smsRequestBody.getMessage()).build();

        //then
        ArgumentCaptor<SmsRequest> smsRequestArgumentCaptor = ArgumentCaptor.forClass(SmsRequest.class);
        Mockito.verify(smsRepository).save(smsRequestArgumentCaptor.capture());

        SmsRequest capturedSmsRequest = smsRequestArgumentCaptor.getValue();

        assertThat(capturedSmsRequest.getPhoneNumber()).isEqualTo(smsRequest.getPhoneNumber());
        assertThat(capturedSmsRequest.getMessage()).isEqualTo(smsRequest.getMessage());
    }

    @Test
    void invalidPhoneNumberTest(){
        //given
        SmsRequestBody smsRequestBody = SmsRequestBody.builder().phoneNumber("+9143210").message("hello").build();

        assertThatThrownBy(()-> underTest.addNewSmsRequest(smsRequestBody))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Phone number is invalid!");
        ;
    }

    @Test
    void emptyPhoneNumberTest(){

        SmsRequestBody smsRequestBody = SmsRequestBody.builder().phoneNumber("").message("hello").build();

        assertThatThrownBy(()-> underTest.addNewSmsRequest(smsRequestBody))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Phone number can not be empty!");
        ;
    }

    @Test
    void blankMessageTest(){
        SmsRequestBody smsRequestBody = SmsRequestBody.builder().phoneNumber("+919876543210").message("").build();

        assertThatThrownBy(()-> underTest.addNewSmsRequest(smsRequestBody))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Message can not be empty!");
        ;
    }

    @Test
    void deleteSmsRequest() {
        //given
        SmsRequest smsRequest = SmsRequest.builder().id(1).phoneNumber("+919876543210").message("hello").build();
        Mockito.when(smsRepository.existsById(1)).thenReturn(Boolean.TRUE);

        //when
        underTest.deleteSmsRequest(1);

        //then
        Mockito.verify(smsRepository,times(1)).deleteById(1);
    }

    @Test
    void getSmsDetailsTest() {
        //given
        SmsRequest smsRequest = SmsRequest.builder().id(1).phoneNumber("+919876543210").message("hello").build();

        Mockito.when(smsRepository.findById(1)).thenReturn(Optional.ofNullable(smsRequest));
        underTest.getSmsDetails(1);
        Mockito.verify(smsRepository).findById(1);
    }

    @Test
    void requestIdNotFoundTest(){

        Mockito.when(smsRepository.findById(1)).thenReturn(Optional.ofNullable(null));
        assertThatThrownBy(() -> underTest.getSmsDetails(1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Request id not found!");
    }
}