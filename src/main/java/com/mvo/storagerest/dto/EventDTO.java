package com.mvo.storagerest.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mvo.storagerest.entity.Status;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EventDTO {
    private long id;
    private Status status;
    private Long userId;
    private Long fileId;
}
