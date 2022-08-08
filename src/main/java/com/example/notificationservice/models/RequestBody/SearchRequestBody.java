package com.example.notificationservice.models.RequestBody;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestBody {
    private String phoneNumber;
    private String message;
    private String startTime;
    private String endTime;
}
