package com.mvo.storagerest.mapper;

import com.mvo.storagerest.dto.UserDTO;
import com.mvo.storagerest.entity.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO map(User user);

    @InheritInverseConfiguration
    User map(UserDTO userDTO);
}
