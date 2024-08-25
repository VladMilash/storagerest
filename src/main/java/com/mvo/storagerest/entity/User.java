package com.mvo.storagerest.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("user")
public class User {
    @Id
    private Long id;

    @Column("username")
    private String userName;

    @Column("password")
    @ToString.Exclude
    private String password;

    @Column("status")
    private Status status;

    @Column("role")
    private UserRole role;

}
