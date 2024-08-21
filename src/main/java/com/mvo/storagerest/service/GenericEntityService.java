package com.mvo.storagerest.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GenericEntityService<E, ID> {
    Mono<E> getById(ID id);

    Flux<E> getAll();

    Mono<E> save(E entity);

    Mono<E> update(E entity);

    Mono<Void> deleteById(ID id);
}
