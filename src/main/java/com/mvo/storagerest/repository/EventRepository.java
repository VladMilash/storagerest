package com.mvo.storagerest.repository;

import com.mvo.storagerest.entity.Event;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface EventRepository extends R2dbcRepository<Event,Long> {
    Flux<Event> findByUserId (long userId);
}
