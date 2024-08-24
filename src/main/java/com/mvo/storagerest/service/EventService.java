package com.mvo.storagerest.service;

import com.mvo.storagerest.entity.Event;
import reactor.core.publisher.Flux;

public interface EventService extends GenericEntityService<Event, Long> {
    Flux<Event> findByUserId (long userId);
}
