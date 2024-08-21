package com.mvo.storagerest.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.mvo.storagerest.entity.File;
import reactor.core.publisher.Mono;

import java.io.InputStream;

public interface FileService extends GenericEntityService<File, Long> {
    Mono<File> uploadFile(String bucketName, String objectName, InputStream inputStream, ObjectMetadata metadata, Long userId);
    Mono<Void> deleteFile(String bucketName, String objectName, Long fileId);
}
