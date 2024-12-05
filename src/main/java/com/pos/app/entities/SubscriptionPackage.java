package com.pos.app.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigInteger;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subscription_package")
public class SubscriptionPackage extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "duration_per_day")
    private BigInteger durationPerDay;

    @Column(name = "price")
    private BigInteger price;

    @Column(name = "description")
    private String description;

}
