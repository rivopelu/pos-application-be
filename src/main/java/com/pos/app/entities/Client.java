package com.pos.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "client")
public class Client {
    @Id
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "note")
    private String note;

    @Column(name = "logo")
    private String logo;

    @Column(name = "created_date")
    private Long createdDate;
    @Column(name = "updated_date")
    private Long updatedDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "is_active_subscription")
    private Boolean isActiveSubscription;


    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "subscription_expired_date")
    private Long subscriptionExpiredDate;


    @ManyToMany
    @JoinTable(name = "client_account", joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id"))
    List<Account> category = new ArrayList<>();


    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        this.createdDate = new Date().getTime();
        this.updatedDate = new Date().getTime();
        this.isActive = true;

    }

    @PreUpdate
    public void preUpdate() {

        this.updatedDate = new Date().getTime();
    }
}
