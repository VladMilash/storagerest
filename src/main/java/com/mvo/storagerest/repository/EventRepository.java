package com.mvo.storagerest.repository;

import com.mvo.storagerest.entity.Event;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface EventRepository extends R2dbcRepository<Event,Long> {
}
