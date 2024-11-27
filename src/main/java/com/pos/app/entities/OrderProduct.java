package com.pos.app.entities;


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
@Table(name = "order_product")
public class OrderProduct {

    @Id
    private String id;

    @Column(name = "qty")
    private BigInteger qty;

    @Column(name = "total_price")
    private BigInteger totalPrice;

    @Column(name = "price_per_qty")
    private BigInteger pricePerQty;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "updated_date")
    private Long updatedDate;

    @Column(name = "created_by")
    private String createdBy;


    @Column(name = "client_id")
    private String clientId;


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
