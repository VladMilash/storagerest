package com.mvo.storagerest.security;

import com.mvo.storagerest.entity.Status;
import com.mvo.storagerest.repository.UserRepository;
import com.mvo.storagerest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SecurityService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public Mono<TokenDetails> authenticate(String username, String password) {
        return userService.findByUserName(username)
                .flatMap(user -> {
                    if (user.getStatus().equals(Status.DELETED)) {
                        return Mono.error(new RuntimeException());
                    }

                    if (passwordEncoder.matches(password, user.getPassword())) {
                        return Mono.error(new RuntimeException());
                    }

                    return Mono.just(new TokenDetails());
                })
                .switchIfEmpty(Mono.error(new RuntimeException()));
    }

}
