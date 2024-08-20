package com.mvo.storagerest.mapper;

import com.mvo.storagerest.dto.FileDTO;
import com.mvo.storagerest.entity.File;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMapper {
    FileDTO map(File file);

    @InheritInverseConfiguration
    File map(FileDTO fileDTO);
}
