package com.mvo.storagerest.service.impl;

import com.mvo.storagerest.entity.Status;
import com.mvo.storagerest.entity.User;
import com.mvo.storagerest.entity.UserRole;
import com.mvo.storagerest.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;


    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .userName("Ivan")
                .password("password")
                .role(UserRole.USER)
                .status(Status.ACTIVE)
                .build();
    }

    @Test
    void findByUsername() {
        when(userRepository.findByUserName("Ivan")).thenReturn(Mono.just(testUser));

        Mono<User> userMono = userService.findByUsername("Ivan");

        StepVerifier.create(userMono)
                .expectNext(testUser)
                .verifyComplete();

        verify(userRepository, times(1)).findByUserName("Ivan");
    }

    @Test
    void getById() {
        when(userRepository.findById(1L)).thenReturn(Mono.just(testUser));

        Mono<User> userMono = userService.getById(1L);

        StepVerifier.create(userMono)
                .expectNext(testUser)
                .verifyComplete();

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getAll() {
        when(userRepository.findAll()).thenReturn(Flux.just(testUser));

        Flux<User> userFlux = userService.getAll();

        StepVerifier.create(userFlux)
                .expectNext(testUser)
                .verifyComplete();

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void save() {
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(testUser));

        Mono<User> userMono = userService.save(testUser);

        StepVerifier.create(userMono)
                .expectNext(testUser)
                .verifyComplete();

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void update() {
        Long userId = testUser.getId();
        User updatedUser = new User(testUser.getId(), "UpdatedIvan", testUser.getPassword(), Status.ACTIVE, UserRole.ADMIN);

        when(userRepository.findById(userId)).thenReturn(Mono.just(testUser));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(updatedUser));

        Mono<User> updatedUserMono = userService.update(updatedUser);

        StepVerifier.create(updatedUserMono)
                .expectNextMatches(user ->
                        user.getId().equals(userId) &&
                                user.getUserName().equals("UpdatedIvan") &&
                                user.getStatus().equals(Status.ACTIVE) &&
                                user.getRole().equals(UserRole.ADMIN) &&
                                user.getPassword().equals("password")
                )
                .verifyComplete();

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    void deleteById() {
        when(userRepository.deleteById(1L)).thenReturn(Mono.empty());

        Mono<Void> result = userService.deleteById(1L);

        StepVerifier.create(result)
                .verifyComplete();

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void registerUser() {
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(testUser));


        Mono<User> userMono = userService.registerUser(testUser);

        StepVerifier.create(userMono)
                .expectNextMatches(user ->
                        user.getId().equals(testUser.getId()) &&
                                user.getUserName().equals("Ivan") &&
                                user.getStatus().equals(Status.ACTIVE) &&
                                user.getRole().equals(UserRole.USER) &&
                                user.getPassword().equals("password")
                )
                .verifyComplete();

        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    void deactivateUser() {
        when(userRepository.findById(1L)).thenReturn(Mono.just(testUser));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(testUser.toBuilder().status(Status.DELETED).build()));

        Mono<User> userMono = userService.deactivateUser(1L);

        StepVerifier.create(userMono)
                .expectNextMatches(user -> user.getStatus().equals(Status.DELETED))
                .verifyComplete();

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }
}
