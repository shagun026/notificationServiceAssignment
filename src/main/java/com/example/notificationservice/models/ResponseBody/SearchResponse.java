package com.example.notificationservice.models.ResponseBody;

import com.example.notificationservice.entities.SearchEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {
    private List<SearchEntity> searchResponse;
}
