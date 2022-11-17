package com.egs.eval.atm.dal.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
public class BaseEntity {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id()
    @Column(name = "id")
    protected Long id;

    @Version
    @Column(name = "version")
    protected Long version;

    @Column(name = "deleted")
    protected Boolean deleted = false;

    @CreatedDate
    @Column(name = "created_date")
    protected LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    protected LocalDateTime lastModifiedDate;

}
