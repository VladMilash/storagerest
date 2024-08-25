package com.mvo.storagerest.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("event")
public class Event {

    @Id
    private Long id;

    @Column("status")
    private Status status;

    @Column("user_id")
    private Long userId;

    @Column("file_id")
    private Long fileId;

}
