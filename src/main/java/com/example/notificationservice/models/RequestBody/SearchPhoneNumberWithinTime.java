package com.example.notificationservice.models.RequestBody;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchPhoneNumberWithinTime {
    private String phoneNumber;
    private String startTime;
    private String endTime;
}
