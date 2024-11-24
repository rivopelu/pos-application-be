package com.pos.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category")
public class Category {
    @Id
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "logo")
    private String logo;

    @Column(name = "seq")
    private Integer seq;

    @Column(name = "created_date")
    private Long createdDate;
    @Column(name = "updated_date")
    private Long updatedDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        this.createdDate = new Date().getTime();
        this.updatedDate = new Date().getTime();
    }

    @PreUpdate
    public void preUpdate() {

        this.updatedDate = new Date().getTime();
    }
}