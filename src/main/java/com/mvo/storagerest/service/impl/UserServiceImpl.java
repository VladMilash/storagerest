package com.mvo.storagerest.service.impl;

import com.mvo.storagerest.entity.Status;
import com.mvo.storagerest.entity.User;
import com.mvo.storagerest.entity.UserRole;
import com.mvo.storagerest.repository.UserRepository;
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
    public Mono<User> findByUsername(String username) {
        return userRepository.findByUserName(username)
                .doOnSuccess(user -> log.info("User with username {} has been finding successfully", username))
                .doOnError(error -> log.error("Failed to find user with username {}", username, error));
    }

    @Override
    public Mono<User> getById(Long id) {
        return userRepository.findById(id)
                .doOnSuccess(user -> log.info("User with id {} has been finding successfully", id))
                .doOnError(error -> log.error("Failed to finding user with id {}", id, error));
    }

    @Override
    public Flux<User> getAll() {
        return userRepository.findAll()
                .doOnNext(user -> log.info("Users has been finding successfully"))
                .doOnError(error -> log.error("Failed to finding users", error));
    }

    @Override
    public Mono<User> save(User entity) {
        return userRepository.save(entity)
                .doOnSuccess(user -> log.info("User with id {} has been saved successfully", user.getId()))
                .doOnError(error -> log.error("Failed to saving user", error));
    }

    @Override
    public Mono<User> update(User entity) {
        return userRepository.findById(entity.getId())
                .map(user -> {
                    log.info("Updating user with id {}", user.getId());
                    user.setUserName(entity.getUserName());
                    user.setRole(entity.getRole());
                    user.setStatus(entity.getStatus());
                    if (!user.getPassword().equals(entity.getPassword())) {
                        log.info("Password for user with id {} is being changed", entity.getId());
                        user.setPassword(passwordEncoder.encode(entity.getPassword()));
                    }
                    return user;
                })
                .flatMap(userRepository::save)
                .doOnSuccess(user -> log.info("User with id {} has been updated successfully", user.getId()))
                .doOnError(error -> log.error("Failed to update user with id {}", entity.getId(), error));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return userRepository.deleteById(id)
                .doOnSuccess(aVoid -> log.info("User with id {} has been deleted successfully", id))
                .doOnError(error -> log.error("Failed to deleted user with id {}", id, error));
    }

    @Override
    public Mono<User> registerUser(User user) {
        return userRepository.save(
                        user.toBuilder()
                                .password(passwordEncoder.encode(user.getPassword()))
                                .role(UserRole.USER)
                                .status(Status.ACTIVE)
                                .build())
                .doOnSuccess(u -> log.info("User with id {} has been registered successfully", u.getId()))
                .doOnError(error -> log.error("Failed to register user with id {}", user.getId(), error));
    }

    @Override
    public Mono<User> deactivateUser(Long id) {
        return userRepository.findById(id)
                .flatMap(user -> {
                    user.setStatus(Status.DELETED);
                    return userRepository.save(user);
                })
                .doOnSuccess(user -> log.info("User with id {} has been deactivated", id))
                .doOnError(error -> log.error("Failed to deactivate user with id {}", id, error));
    }
}
