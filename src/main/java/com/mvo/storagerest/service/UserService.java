package com.mvo.storagerest.service;

import com.mvo.storagerest.entity.User;
import reactor.core.publisher.Mono;

public interface UserService extends GenericEntityService<User, Long> {
    Mono<User> findByUsername(String username);
    Mono<User> registerUser(User user);
    Mono<User> deactivateUser(Long id);
}
