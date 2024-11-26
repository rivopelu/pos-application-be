package com.pos.app.entities;


import com.pos.app.enums.OrderStatusEnum;
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
@Table(name = "order")
public class Order {
    @Id
    private String id;

    @Column(name = "order_code")
    private String orderCode;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "updated_date")
    private Long updatedDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;

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
