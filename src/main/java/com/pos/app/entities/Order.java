package com.pos.app.entities;


import com.pos.app.enums.OrderStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`order`")
public class Order {
    @Id
    private String id;

    @Column(name = "order_code")
    private BigInteger orderCode;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "updated_date")
    private Long updatedDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;

    @Column(name = "is_payment")
    private Boolean isPayment;

    @Column(name = "client_id")
    private String clientId;


    @Column(name = "is_active")
    private Boolean isActive;

    @JoinColumn(name = "customer_table")
    @ManyToOne
    private CustomerTable customerTable;

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
