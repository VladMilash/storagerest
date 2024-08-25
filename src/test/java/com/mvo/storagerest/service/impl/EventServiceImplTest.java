package com.mvo.storagerest.service.impl;

import com.mvo.storagerest.entity.Event;
import com.mvo.storagerest.entity.Status;
import com.mvo.storagerest.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event testEvent;

    @BeforeEach
    void setUp() {
        testEvent = Event.builder()
                .id(1L)
                .userId(1L)
                .fileId(1L)
                .status(Status.ACTIVE)
                .build();
    }

    @Test
    void getById() {
        when(eventRepository.findById(anyLong())).thenReturn(Mono.just(testEvent));

        Mono<Event> eventMono = eventService.getById(1L);

        StepVerifier.create(eventMono)
                .expectNext(testEvent)
                .verifyComplete();

        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    void getAll() {
        when(eventRepository.findAll()).thenReturn(Flux.just(testEvent));

        Flux<Event> eventFlux = eventService.getAll();

        StepVerifier.create(eventFlux)
                .expectNext(testEvent)
                .verifyComplete();

        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void save() {
        when(eventRepository.save(any(Event.class))).thenReturn(Mono.just(testEvent));

        Mono<Event> eventMono = eventService.save(testEvent);

        StepVerifier.create(eventMono)
                .expectNext(testEvent)
                .verifyComplete();

        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void update() {
        Event updatedEvent = testEvent.toBuilder().status(Status.DELETED).build();

        when(eventRepository.findById(anyLong())).thenReturn(Mono.just(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(Mono.just(updatedEvent));

        Mono<Event> updatedEventMono = eventService.update(updatedEvent);

        StepVerifier.create(updatedEventMono)
                .expectNextMatches(event ->
                        event.getId().equals(testEvent.getId()) &&
                                event.getStatus().equals(Status.DELETED)
                )
                .verifyComplete();

        verify(eventRepository, times(1)).findById(anyLong());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void deleteById() {
        when(eventRepository.deleteById(anyLong())).thenReturn(Mono.empty());

        Mono<Void> result = eventService.deleteById(1L);

        StepVerifier.create(result)
                .verifyComplete();

        verify(eventRepository, times(1)).deleteById(1L);
    }

    @Test
    void findByUserId() {
        when(eventRepository.findByUserId(anyLong())).thenReturn(Flux.just(testEvent));

        Flux<Event> eventFlux = eventService.findByUserId(1L);

        StepVerifier.create(eventFlux)
                .expectNext(testEvent)
                .verifyComplete();

        verify(eventRepository, times(1)).findByUserId(1L);
    }
}
