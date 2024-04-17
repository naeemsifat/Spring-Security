package com.security.task.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.security.task.enums.RecordStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@MappedSuperclass
public abstract class BaseEntity implements Serializable {
    private static final Long serialVersionUID = 1L;

//    @Column(name = "APP_ENTITY_ID",columnDefinition = "integer default 113")
//    private Integer appEntityId;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT", updatable = false)
    protected Date createdAt;


    @Column(name = "CREATED_BY", updatable = false, columnDefinition = "uniqueidentifier")
    private Integer createdBy;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_AT")
    protected Date updatedAt;

    //default one, auto increment for each operation like update

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private RecordStatus recordStatus;
    @PrePersist
    public void prePersist() {
        this.createdAt = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
}
