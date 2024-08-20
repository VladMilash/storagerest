package com.mvo.storagerest.repository;

import com.mvo.storagerest.entity.File;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface FileRepository extends R2dbcRepository<File,Long> {
}
