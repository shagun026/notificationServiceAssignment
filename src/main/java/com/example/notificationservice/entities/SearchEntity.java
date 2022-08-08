package com.example.notificationservice.entities;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.*;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Document(indexName = "sms_details")
@Setting(settingPath = "static/es-settings.json")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchEntity {

    @Id
    private Integer id;
    private String phoneNumber;
    private String message;
    private String status;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;

}
