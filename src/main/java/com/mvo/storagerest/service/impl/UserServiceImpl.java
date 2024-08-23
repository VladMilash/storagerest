package com.mvo.storagerest.service.impl;

import com.mvo.storagerest.entity.Status;
import com.mvo.storagerest.entity.User;
import com.mvo.storagerest.entity.UserRole;
import com.mvo.storagerest.repository.UserRepository;
import com.mvo.storagerest.security.PBFDK2Encoder;
import com.mvo.storagerest.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<User> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public Mono<User> getById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Flux<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public Mono<User> save(User entity) {
        return userRepository.save(entity);
    }

    @Override
    public Mono<User> update(User entity) {
        return userRepository.findById(entity.getId())
                .map(user -> {
                    user.setUserName(entity.getUserName());
                    user.setRole(entity.getRole());
                    user.setStatus(entity.getStatus());
                    user.setPassword(entity.getPassword());
                    user.setEvents(entity.getEvents());
                    return user;
                })
                .flatMap(userRepository::save);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return userRepository.deleteById(id);
    }

    @Override
    public Mono<User> registerUser(User user) {
        return userRepository.save(
                user.toBuilder()
                        .password(passwordEncoder.encode(user.getPassword()))
                        .role(UserRole.USER)
                        .status(Status.ACTIVE)
                        .build()
        ).doOnSuccess(u -> {
            log.info("In registerUser- user: {} created", u);
        });
    }

}
