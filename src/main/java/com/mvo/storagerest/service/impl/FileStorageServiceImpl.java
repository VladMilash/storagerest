package com.mvo.storagerest.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.mvo.storagerest.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;

@Log4j2
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final AmazonS3 amazonS3;

    public Mono<String> uploadFile(String bucketName, String objectName, InputStream inputStream, ObjectMetadata metadata) {
        return Mono.fromCallable(() -> {
            try (inputStream) {
                amazonS3.putObject(bucketName, objectName, inputStream, metadata);
                log.info("Successfully uploaded file to S3 with object name: {}", objectName);
                return objectName;
            } catch (Exception e) {
                log.error("Error uploading file to S3 with object name: {}", objectName, e);
                throw new RuntimeException("Error uploading file to S3", e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<S3Object> downloadFile(String bucketName, String objectName) {
        return Mono.fromCallable(() -> {
            try {
                S3Object s3Object = amazonS3.getObject(bucketName, objectName);
                log.info("Successfully downloaded file from S3 with object name: {}", objectName);
                return s3Object;
            } catch (Exception e) {
                log.error("Error downloading file from S3 with object name: {}", objectName, e);
                throw new RuntimeException("Error downloading file from S3", e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteFile(String bucketName, String objectName) {
        return Mono.fromRunnable(() -> {
            try {
                amazonS3.deleteObject(bucketName, objectName);
                log.info("Successfully deleted file from S3 with object name: {}", objectName);
            } catch (Exception e) {
                log.error("Error deleting file from S3 with object name: {}", objectName, e);
                throw new RuntimeException("Error deleting file from S3", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
