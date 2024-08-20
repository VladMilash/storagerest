package com.mvo.storagerest.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mvo.storagerest.entity.Status;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FileDTO {
    private long id;
    private String location;
    private Status status;
}
