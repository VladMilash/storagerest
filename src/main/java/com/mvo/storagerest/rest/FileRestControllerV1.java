package com.mvo.storagerest.rest;

import com.amazonaws.services.s3.model.S3Object;
import com.mvo.storagerest.dto.FileDTO;
import com.mvo.storagerest.entity.File;
import com.mvo.storagerest.mapper.FileMapper;
import com.mvo.storagerest.security.SecurityService;
import com.mvo.storagerest.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/files")
public class FileRestControllerV1 {
    private final FileService fileService;
    private final FileMapper fileMapper;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    public Mono<ResponseEntity<FileDTO>> getFileById(@PathVariable("id") Long id) {
        return fileService.getById(id)
                .map(fileMapper::map)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    public Flux<FileDTO> getAllFiles() {
        return fileService.getAll()
                .map(fileMapper::map);
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR', 'USER')")
    public Mono<ResponseEntity<FileDTO>> uploadFile(@RequestParam("bucketName") String bucketName,
                                                    @RequestParam("objectName") String objectName,
                                                    @RequestBody InputStream inputStream,
                                                    @RequestParam("userId") Long userId) {
        return fileService.uploadFile(bucketName, objectName, inputStream, null, userId)
                .map(fileMapper::map)
                .map(savedFile -> ResponseEntity.status(HttpStatus.CREATED).body(savedFile));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    public Mono<ResponseEntity<FileDTO>> update(@PathVariable("id") Long id, @RequestBody FileDTO fileDTO) {
        if (!id.equals(fileDTO.getId())) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        File file = fileMapper.map(fileDTO);
        return fileService.update(file)
                .map(fileMapper::map)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") Long id) {
        return fileService.getById(id)
                .flatMap(file -> fileService.deleteById(file.getId())
                        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
                .switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    @DeleteMapping("/s3/{bucketName}/{objectName}/{fileId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    public Mono<ResponseEntity<Void>> deleteFile(@PathVariable("bucketName") String bucketName,
                                                 @PathVariable("objectName") String objectName,
                                                 @PathVariable("fileId") Long fileId) {
        return fileService.deleteFile(bucketName, objectName, fileId)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/download/{bucketName}/{objectName}/{fileId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    public Mono<ResponseEntity<S3Object>> downloadFile(@PathVariable("bucketName") String bucketName,
                                                       @PathVariable("objectName") String objectName,
                                                       @PathVariable("fileId") Long fileId) {
        return fileService.downloadFile(bucketName, objectName, fileId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
