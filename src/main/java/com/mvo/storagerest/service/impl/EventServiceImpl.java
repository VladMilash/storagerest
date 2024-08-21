package com.mvo.storagerest.service.impl;

import com.mvo.storagerest.entity.Event;
import com.mvo.storagerest.repository.EventRepository;
import com.mvo.storagerest.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    @Override
    public Mono<Event> getById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public Flux<Event> getAll() {
        return eventRepository.findAll();
    }

    @Override
    public Mono<Event> save(Event entity) {
        return eventRepository.save(entity);
    }

    @Override
    public Mono<Event> update(Event entity) {
        return eventRepository.findById(entity.getId())
                .map(event -> {
                    event.setFileId(entity.getFileId());
                    event.setUserId(entity.getUserId());
                    event.setStatus(entity.getStatus());
                    return event;
                })
                .flatMap(eventRepository::save);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return eventRepository.deleteById(id);
    }
}
