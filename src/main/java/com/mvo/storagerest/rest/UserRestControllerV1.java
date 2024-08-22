package com.mvo.storagerest.rest;

import com.mvo.storagerest.dto.UserDTO;
import com.mvo.storagerest.mapper.UserMapper;
import com.mvo.storagerest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users/")
public class UserRestControllerV1 {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserDTO>> getUserById(@PathVariable("id") Long id) {
        return userService
                .getById(id)
                .map(userMapper::map)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux<UserDTO> getAllUsers() {
        return userService
                .getAll()
                .map(userMapper::map);
    }

}
