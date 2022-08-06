package com.example.notificationservice.models.RequestBody;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SmsRequestBody {
    private String phoneNumber;
    private String message;
}
