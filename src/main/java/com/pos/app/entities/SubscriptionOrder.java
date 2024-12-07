package com.pos.app.entities;


import com.pos.app.enums.SubscriptionOrderStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subscription_order")
public class SubscriptionOrder extends BaseEntity {

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private SubscriptionOrderStatusEnum status;

    @Column(name = "total_transaction")
    private BigInteger totalTransaction;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "package_id")
    private SubscriptionPackage subscriptionPackage;

}
