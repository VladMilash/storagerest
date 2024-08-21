package com.mvo.storagerest.service;

import com.mvo.storagerest.entity.User;
import reactor.core.publisher.Mono;

public interface UserService extends GenericEntityService<User, Long> {
    Mono<User> findByUserName(String userName);
}
