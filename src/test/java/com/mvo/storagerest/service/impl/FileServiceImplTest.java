package com.mvo.storagerest.service.impl;

import com.mvo.storagerest.entity.File;
import com.mvo.storagerest.entity.Status;
import com.mvo.storagerest.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileServiceImpl fileService;

    private File testFile;

    @BeforeEach
    void setUp() {
        testFile = File.builder()
                .id(1L)
                .location("s3://bucket/test-file")
                .status(Status.ACTIVE)
                .build();
    }

    @Test
    void getById() {
        when(fileRepository.findById(anyLong())).thenReturn(Mono.just(testFile));

        Mono<File> fileMono = fileService.getById(1L);

        StepVerifier.create(fileMono)
                .expectNext(testFile)
                .verifyComplete();

        verify(fileRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAll() {
        when(fileRepository.findAll()).thenReturn(Flux.just(testFile));

        Flux<File> fileFlux = fileService.getAll();

        StepVerifier.create(fileFlux)
                .expectNext(testFile)
                .verifyComplete();

        verify(fileRepository, times(1)).findAll();
    }

    @Test
    void save() {
        when(fileRepository.save(any(File.class))).thenReturn(Mono.just(testFile));

        Mono<File> fileMono = fileService.save(testFile);

        StepVerifier.create(fileMono)
                .expectNext(testFile)
                .verifyComplete();

        verify(fileRepository, times(1)).save(any(File.class));
    }

    @Test
    void update() {
        File updatedFile = testFile.toBuilder().status(Status.DELETED).build();

        when(fileRepository.findById(anyLong())).thenReturn(Mono.just(testFile));
        when(fileRepository.save(any(File.class))).thenReturn(Mono.just(updatedFile));

        Mono<File> updatedFileMono = fileService.update(updatedFile);

        StepVerifier.create(updatedFileMono)
                .expectNextMatches(file ->
                        file.getId().equals(testFile.getId()) &&
                                file.getStatus().equals(Status.DELETED)
                )
                .verifyComplete();

        verify(fileRepository, times(1)).findById(anyLong());
        verify(fileRepository, times(1)).save(any(File.class));
    }

    @Test
    void deleteById() {
        when(fileRepository.deleteById(anyLong())).thenReturn(Mono.empty());

        Mono<Void> result = fileService.deleteById(1L);

        StepVerifier.create(result)
                .verifyComplete();

        verify(fileRepository, times(1)).deleteById(1L);
    }
}
