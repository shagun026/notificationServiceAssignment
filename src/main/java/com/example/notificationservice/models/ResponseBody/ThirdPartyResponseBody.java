package com.example.notificationservice.models.ResponseBody;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThirdPartyResponseBody {


    @JsonProperty("response")
    private ArrayList<Response> response;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response{
        @JsonProperty("code")
        private String code;

        @JsonProperty("transid")
        private String transid;

        @JsonProperty("description")
        private String description;

        @JsonProperty("correlation id")
        private String correlationid;

    }
}
