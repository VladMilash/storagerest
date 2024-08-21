package com.mvo.storagerest.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.mvo.storagerest.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final AmazonS3 amazonS3;

    @Autowired
    public FileStorageServiceImpl(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public Mono<String> uploadFile(String bucketName, String objectName, InputStream inputStream, ObjectMetadata metadata) {
        return Mono.fromCallable(() -> {
            try (inputStream) {
                amazonS3.putObject(bucketName, objectName, inputStream, metadata);
                return objectName;
            } catch (Exception e) {
                throw new RuntimeException("Error uploading file to S3", e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<S3Object> downloadFile(String bucketName, String objectName) {
        return Mono.fromCallable(() -> {
            try {
                return amazonS3.getObject(bucketName, objectName);
            } catch (Exception e) {
                throw new RuntimeException("Error downloading file from S3", e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteFile(String bucketName, String objectName) {
        return Mono.fromRunnable(() -> {
            try {
                amazonS3.deleteObject(bucketName, objectName);
            } catch (Exception e) {
                throw new RuntimeException("Error deleting file from S3", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
