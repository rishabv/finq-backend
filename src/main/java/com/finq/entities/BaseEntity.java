package com.finq.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;

    @CreatedDate
    protected LocalDateTime creationDate;

    @LastModifiedDate
    protected LocalDateTime lastModifiedDate;
    protected boolean isDeleted = false;

    @Column(name = "is_active")
    protected boolean isActive = Boolean.TRUE;
}
