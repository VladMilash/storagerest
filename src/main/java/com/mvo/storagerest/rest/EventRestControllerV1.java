package com.mvo.storagerest.rest;

import com.mvo.storagerest.dto.EventDTO;
import com.mvo.storagerest.entity.Event;
import com.mvo.storagerest.mapper.EventMapper;
import com.mvo.storagerest.security.CustomPrincipal;
import com.mvo.storagerest.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/events")
public class EventRestControllerV1 {
    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    public Mono<ResponseEntity<EventDTO>> getEventById(@PathVariable("id") Long id) {
        return eventService.getById(id)
                .map(eventMapper::map)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    public Flux<EventDTO> getAllEvents() {
        return eventService.getAll()
                .map(eventMapper::map);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    public Mono<ResponseEntity<EventDTO>> save(@RequestBody EventDTO eventDTO) {
        Event event = eventMapper.map(eventDTO);
        return eventService.save(event)
                .map(eventMapper::map)
                .map(savedEvent -> ResponseEntity.status(HttpStatus.CREATED).body(savedEvent));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    public Mono<ResponseEntity<EventDTO>> update(@PathVariable("id") Long id, @RequestBody EventDTO eventDTO) {
        if (!id.equals(eventDTO.getId())) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        Event event = eventMapper.map(eventDTO);
        return eventService.update(event)
                .map(eventMapper::map)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") Long id) {
        return eventService.getById(id)
                .flatMap(event -> eventService.deleteById(event.getId())
                        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
                .switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    public Flux<EventDTO> getEventsByUserId(@PathVariable("userId") Long userId) {
        return eventService.findByUserId(userId)
                .map(eventMapper::map);
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR','USER')")
    public Flux<EventDTO> getEvents(Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return eventService.findByUserId(customPrincipal.getId())
                .map(eventMapper::map);
    }
}

