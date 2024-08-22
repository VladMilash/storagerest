package com.mvo.storagerest.service.impl;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.mvo.storagerest.entity.Event;
import com.mvo.storagerest.entity.File;
import com.mvo.storagerest.entity.Status;
import com.mvo.storagerest.repository.EventRepository;
import com.mvo.storagerest.repository.FileRepository;
import com.mvo.storagerest.repository.UserRepository;
import com.mvo.storagerest.service.FileService;
import com.mvo.storagerest.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final FileStorageService fileStorageService;
    private final FileStorageServiceImpl fileStorageServiceImpl;

    @Override
    public Mono<File> getById(Long id) {
        return fileRepository.findById(id);
    }

    @Override
    public Flux<File> getAll() {
        return fileRepository.findAll();
    }

    @Override
    public Mono<File> save(File entity) {
        return fileRepository.save(entity);

    }

    @Override
    public Mono<File> update(File entity) {
        return fileRepository.findById(entity.getId())
                .map(file -> {
                    file.setLocation(entity.getLocation());
                    file.setStatus(entity.getStatus());
                    return file;
                })
                .flatMap(fileRepository::save);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return fileRepository.deleteById(id);
    }

    @Override
    public Mono<File> uploadFile(String bucketName, String objectName, InputStream inputStream, ObjectMetadata metadata, Long userId) {
        return fileStorageService.uploadFile(bucketName, objectName, inputStream, metadata)
                .flatMap(uploadedObjectName -> {
                    File file = new File();
                    file.setLocation("s3://" + bucketName + "/" + uploadedObjectName);
                    file.setStatus(Status.ACTIVE);

                    return fileRepository.save(file)
                            .flatMap(savedFile -> {
                                Event event = new Event();
                                event.setUserId(userId);
                                event.setFileId(savedFile.getId());
                                event.setStatus(Status.ACTIVE);

                                return eventRepository.save(event)
                                        .then(Mono.just(savedFile));
                            });
                });
    }

    @Override
    public Mono<Void> deleteFile(String bucketName, String objectName, Long fileId) {
        return fileRepository.findById(fileId)
                .flatMap(file -> fileStorageService.deleteFile(bucketName, objectName)
                        .then(Mono.fromCallable(() -> {
                            file.setStatus(Status.DELETED);
                            return fileRepository.save(file);
                        }))
                )
                .then();
    }

    @Override
    public Mono<S3Object> downloadFile(String bucketName, String objectName, Long fileId) {
        return fileRepository.findById(fileId)
                .flatMap(file -> fileStorageService.downloadFile(bucketName,objectName));
    }
}