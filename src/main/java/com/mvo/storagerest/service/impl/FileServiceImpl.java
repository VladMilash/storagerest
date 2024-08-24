package com.mvo.storagerest.service.impl;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.mvo.storagerest.entity.Event;
import com.mvo.storagerest.entity.File;
import com.mvo.storagerest.entity.Status;
import com.mvo.storagerest.exception.NotExistException;
import com.mvo.storagerest.repository.EventRepository;
import com.mvo.storagerest.repository.FileRepository;
import com.mvo.storagerest.repository.UserRepository;
import com.mvo.storagerest.service.FileService;
import com.mvo.storagerest.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;

@Log4j2
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final FileStorageService fileStorageService;

    @Override
    public Mono<File> getById(Long id) {
        return fileRepository.findById(id)
                .doOnSuccess(file -> log.info("File with id {} has been finding successfully", id))
                .doOnError(error -> log.error("Failed to finding file with id {}", id, error));
    }

    @Override
    public Flux<File> getAll() {
        return fileRepository.findAll()
                .doOnNext(file -> log.info("Files has been finding successfully"))
                .doOnError(error -> log.error("Failed to finding files", error));
    }

    @Override
    public Mono<File> save(File entity) {
        return fileRepository.save(entity)
                .doOnSuccess(file -> log.info("File with id {} has been saved successfully", file.getId()))
                .doOnError(error -> log.error("Failed to saved file", error));

    }

    @Override
    public Mono<File> update(File entity) {
        return fileRepository.findById(entity.getId())
                .map(file -> {
                    log.info("Updating file with id {}", entity.getId());
                    file.setLocation(entity.getLocation());
                    file.setStatus(entity.getStatus());
                    return file;
                })
                .flatMap(fileRepository::save)
                .doOnSuccess(file -> log.info("File with id {} has been updated successfully", file.getId()))
                .doOnError(error -> log.error("Failed to updated file with id {}", entity.getId(), error));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return fileRepository.deleteById(id)
                .doOnSuccess(aVoid -> log.info("File with id {} has been deleted successfully", id))
                .doOnError(error -> log.error("Failed to delete file with id {}", id, error));

    }

    @Override
    public Mono<File> uploadFile(String bucketName, String objectName, InputStream inputStream, ObjectMetadata metadata, Long userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new NotExistException("User not found with id: " + userId, "MVO_USER_NOT_FOUND")))
                .doOnSuccess(user -> log.info("User with id {} found for file upload", userId))
                .flatMap(user -> fileStorageService.uploadFile(bucketName, objectName, inputStream, metadata)
                        .doOnSuccess(uploadedObjectName -> log.info("File uploaded to S3 with object name: {}", uploadedObjectName))
                        .flatMap(uploadedObjectName -> {
                            File file = new File();
                            file.setLocation("s3://" + bucketName + "/" + uploadedObjectName);
                            file.setStatus(Status.ACTIVE);

                            return fileRepository.save(file)
                                    .doOnSuccess(savedFile -> log.info("File with id {} saved successfully", savedFile.getId()))
                                    .flatMap(savedFile -> {
                                        Event event = new Event();
                                        event.setUserId(userId);
                                        event.setFileId(savedFile.getId());
                                        event.setStatus(Status.ACTIVE);

                                        return eventRepository.save(event)
                                                .doOnSuccess(savedEvent -> log.info("Event for user id {} and file id {} saved successfully", userId, savedFile.getId()))
                                                .then(Mono.just(savedFile));
                                    });
                        }))
                .doOnError(error -> log.error("Failed to upload file for user with id {}", userId, error));
    }

    @Override
    public Mono<Void> deleteFile(String bucketName, String objectName, Long fileId) {
        return fileRepository.findById(fileId)
                .switchIfEmpty(Mono.error(new NotExistException("File not found with id: " + fileId, "MVO_FILE_NOT_FOUND")))
                .flatMap(file -> fileStorageService.deleteFile(bucketName, objectName)
                        .doOnSuccess(unused -> log.info("File deleted from S3 with object name: {}", objectName))
                        .then(Mono.defer(() -> {
                            file.setStatus(Status.DELETED);
                            return fileRepository.save(file);
                        }))
                        .doOnSuccess(savedFile -> log.info("File with id {} status set to DELETED", savedFile.getId()))
                )
                .then()
                .doOnError(error -> log.error("Failed to delete file for file id {}", fileId, error));
    }


    @Override
    public Mono<S3Object> downloadFile(String bucketName, String objectName, Long fileId) {
        return fileRepository.findById(fileId)
                .switchIfEmpty(Mono.error(new NotExistException("File not found with id: " + fileId, "MVO_FILE_NOT_FOUND")))
                .flatMap(file -> fileStorageService.downloadFile(bucketName, objectName))
                .doOnSuccess(s3Object -> log.info("File downloaded from S3 with object name: {}", objectName))
                .doOnError(error -> log.error("Failed to download file for file id {}", fileId, error));
    }

}