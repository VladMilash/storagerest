package com.mvo.storagerest.mapper;

import com.mvo.storagerest.dto.EventDTO;
import com.mvo.storagerest.entity.Event;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventDTO map(Event event);

    @InheritInverseConfiguration
    Event map(EventDTO eventDTO);
}
