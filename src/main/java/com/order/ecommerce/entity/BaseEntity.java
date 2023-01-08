package com.order.ecommerce.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDate;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity implements Serializable {
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDate createdAt;
    BaseEntity(){}

    // TO-DO updated_at, created_by, updated_by
}
