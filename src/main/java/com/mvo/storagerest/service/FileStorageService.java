package com.mvo.storagerest.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import reactor.core.publisher.Mono;

import java.io.InputStream;

public interface FileStorageService {
    Mono<String> uploadFile(String bucketName, String objectName, InputStream inputStream, ObjectMetadata metadata);

    Mono<S3Object> downloadFile(String bucketName, String objectName);

    Mono<Void> deleteFile(String bucketName, String objectName);
}
