package com.mvo.storagerest.service.impl;

import com.mvo.storagerest.entity.Event;
import com.mvo.storagerest.repository.EventRepository;
import com.mvo.storagerest.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    @Override
    public Mono<Event> getById(Long id) {
        return eventRepository.findById(id)
                .doOnSuccess(event -> log.info("Event with id {} has been find successfully", id))
                .doOnError(error -> log.error("Failed to find event with id {}", id, error));
    }

    @Override
    public Flux<Event> getAll() {
        return eventRepository.findAll()
                .doOnNext(event -> log.info("Events has been find successfully"))
                .doOnError(error -> log.error("Failed to find events ", error));
    }

    @Override
    public Mono<Event> save(Event entity) {
        return eventRepository.save(entity)
                .doOnSuccess(event -> log.info("Event with id {} has been saved successfully", event.getId()))
                .doOnError(error -> log.error("Failed to save event ", error));
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
                .flatMap(eventRepository::save)
                .doOnSuccess(event -> log.info("Event with id {} has been updated successfully", event.getId()))
                .doOnError(error -> log.error("Failed to update event with id {}", entity.getId(), error));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return eventRepository.deleteById(id)
                .doOnSuccess(event -> log.info("Event with id {} has been deleted successfully", id))
                .doOnError(error -> log.error("Failed to delete event with id {}", id, error));
    }

    @Override
    public Flux<Event> findByUserId(long userId) {
        return eventRepository.findByUserId(userId)
                .doOnNext(aVoid -> log.info("Events for User id {} has been find successfully", userId))
                .doOnError(error -> log.error("Failed to finding events for User with id {}", userId, error));
    }
}
