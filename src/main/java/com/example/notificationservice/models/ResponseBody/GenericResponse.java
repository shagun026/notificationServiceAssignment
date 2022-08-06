package com.example.notificationservice.models.ResponseBody;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenericResponse {
    private Object data;
    private Error error;
}
