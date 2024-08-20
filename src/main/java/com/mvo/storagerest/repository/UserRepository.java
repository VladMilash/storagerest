package com.mvo.storagerest.repository;

import com.mvo.storagerest.entity.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<User,Long> {
    Mono<User> findByUserName(String userName);
}
