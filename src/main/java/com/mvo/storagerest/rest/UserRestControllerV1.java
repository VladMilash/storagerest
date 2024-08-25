package com.mvo.storagerest.rest;

import com.mvo.storagerest.dto.UserDTO;
import com.mvo.storagerest.entity.User;
import com.mvo.storagerest.mapper.UserMapper;
import com.mvo.storagerest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users/")
public class UserRestControllerV1 {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    public Mono<ResponseEntity<UserDTO>> getUserById(@PathVariable("id") Long id) {
        return userService
                .getById(id)
                .map(userMapper::map)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    public Flux<UserDTO> getAllUsers() {
        return userService
                .getAll()
                .map(userMapper::map);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") Long id) {
        return userService.getById(id)
                .flatMap(user -> userService.deleteById(user.getId()))
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    @GetMapping("/by-username/{username}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    public Mono<ResponseEntity<UserDTO>> getByUsername(@PathVariable("username") String username) {
        return userService
                .findByUsername(username)
                .map(userMapper::map)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ResponseEntity<UserDTO>> save(@RequestBody UserDTO userDTO) {
        User user = userMapper.map(userDTO);
        return userService.save(user)
                .map(userMapper::map)
                .map(savedUser -> ResponseEntity.status(HttpStatus.CREATED).body(savedUser));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ResponseEntity<UserDTO>> update(@PathVariable("id") Long id, @RequestBody UserDTO userDTO) {
        if (!id.equals(userDTO.getId())) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        User user = userMapper.map(userDTO);
        return userService.update(user)
                .map(userMapper::map)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/deactivate/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ResponseEntity<UserDTO>> deactivateUser(@PathVariable("id") Long id) {
        return userService
                .getById(id)
                .flatMap(user -> userService.deactivateUser(user.getId()))
                .map(userMapper::map)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
